# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project

Minecraft Fabric 1.20.1 mod (Java 17) — UE5-style blockout/greybox blocks for level design prototyping. Mod ID: `blockouttools`.

## Build Commands

- Build: `./gradlew build`
- Run client: `./gradlew runClient`
- Run data generators: `./gradlew runDatagen`
- No test suite exists. Verification is done by running the client and testing in-game.

## Architecture (Non-Obvious)

- **Dual-model item rendering**: Items use `ContextAwareBakedModel` (switched via ThreadLocal in `ItemRendererMixin`) to show a different model in GUI vs hand/world. GUI models live in `models/item/gui/`. Adding a new block requires entries in `ITEM_TO_GUI_MODEL_MAP` in `BlockoutToolsClient`.
- **RampBlock rendering**: JSON model at `models/block/blockout_ramp.json` is a dummy — replaced at runtime by `RampBakedModel` (Fabric Renderer API, programmatic triangle mesh). The `inventory` variant is NOT replaced (kept for GUI fallback).
- **Registry name mismatch**: Field `BLOCKOUT_BOX_SLAB` is registered as `"blockout_slab"` (not `"blockout_box_slab"`). Follow existing naming in `BlockoutBlocks.registerBlock()` calls.
- **PlacementPreviewRenderer**: Renders ghost block preview for any block extending `BlockoutToolsBlock`. Automatically active — no per-block setup needed.
- **BlockoutPlacementStateBuilder**: Fluent builder in `util/` for complex placement logic (slope type, corner type, directional slab). Use `BlockoutPlacementStateBuilder.of(block, ctx).withX().build()` in `getPlacementState()`.
- All blocks share one texture: `textures/block/blockout_grid.png`.
- Fabric Loom 1.4-SNAPSHOT with **Yarn mappings** (not Mojmap).

## Code Style

- Comments and Javadoc in Chinese (简体中文).
- Traditional switch statements (not enhanced switch expressions).
- All blocks extend `BlockoutToolsBlock` (which implements `Waterloggable`).
- Non-opaque blocks (slabs, stairs, ramp) need both `.nonOpaque()` in settings AND `BlockRenderLayerMap` entry in `BlockoutToolsClient`.
- VoxelShapes: pre-computed static fields, `Block.createCuboidShape()` with 0-16 pixel coords.
- Identifiers: `new Identifier(BlockoutTools.MOD_ID, name)`.

## Adding a New Block (Checklist)

1. Create block class extending `BlockoutToolsBlock` in `block/impl/`
2. Add static field + registration in `BlockoutBlocks` (field, `register()` call, item group entry)
3. If non-opaque: add `.nonOpaque()` to settings + `BlockRenderLayerMap` entry in client
4. Add GUI model mapping in `ITEM_TO_GUI_MODEL_MAP` if custom inventory appearance needed
5. Create blockstate JSON, block model JSON, item model JSON, and GUI model JSON
