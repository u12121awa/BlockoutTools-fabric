package me.maceaura.blockouttools.client;

import me.maceaura.blockouttools.block.BlockoutBlocks;
import me.maceaura.blockouttools.block.impl.RampBlock;
import me.maceaura.blockouttools.client.model.ContextAwareBakedModel;
import me.maceaura.blockouttools.client.model.RampBakedModel;
import me.maceaura.blockouttools.client.render.EdgeOutlineRenderer;
import me.maceaura.blockouttools.client.render.PlacementPreviewRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BlockoutToolsClient implements ClientModInitializer {
    private static final Map<ModelIdentifier, Identifier> ITEM_TO_GUI_MODEL_MAP = new HashMap<>();

    static {
        ITEM_TO_GUI_MODEL_MAP.put(
                new ModelIdentifier(new Identifier("blockouttools", "blockout_box"), "inventory"),
                new Identifier("blockouttools", "item/gui/blockout_box_gui")
        );
        ITEM_TO_GUI_MODEL_MAP.put(
                new ModelIdentifier(new Identifier("blockouttools", "blockout_slab"), "inventory"),
                new Identifier("blockouttools", "item/gui/blockout_slab_gui")
        );
        ITEM_TO_GUI_MODEL_MAP.put(
                new ModelIdentifier(new Identifier("blockouttools", "blockout_vertical_slab"), "inventory"),
                new Identifier("blockouttools", "item/gui/blockout_vertical_slab_gui")
        );
        ITEM_TO_GUI_MODEL_MAP.put(
                new ModelIdentifier(new Identifier("blockouttools", "blockout_stairs_linear"), "inventory"),
                new Identifier("blockouttools", "item/gui/blockout_stairs_linear_gui")
        );
    }

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BlockoutBlocks.BLOCKOUT_STAIRS_LINEAR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockoutBlocks.BLOCKOUT_BOX_SLAB, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockoutBlocks.BLOCKOUT_VERTICAL_SLAB, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(BlockoutBlocks.BLOCKOUT_RAMP, RenderLayer.getCutoutMipped());

        ModelLoadingPlugin.register(pluginContext -> {
            for (Identifier guiModelId : ITEM_TO_GUI_MODEL_MAP.values()) {
                pluginContext.addModels(guiModelId);
            }

            pluginContext.modifyModelAfterBake().register((model, context) -> {
                if (model == null) return null;

                BakedModel finalModel = model;

                if (context.id() instanceof ModelIdentifier modelId) {
                    if (modelId.getNamespace().equals("blockouttools") && modelId.getPath().equals("blockout_ramp")) {

                        Sprite particleSprite = model.getParticleSprite();
                        Sprite topSprite = extractSpriteFromQuads(model, Direction.UP);
                        if (topSprite == null) topSprite = particleSprite;

                        Identifier boxGuiId = new Identifier("blockouttools", "item/gui/blockout_box_gui");
                        BakedModel boxGuiModel = context.baker().bake(boxGuiId, context.settings());

                        Sprite sideSprite64 = particleSprite;
                        Sprite topSprite64 = topSprite;

                        if (boxGuiModel != null) {
                            Sprite extractedSide = extractSpriteFromQuads(boxGuiModel, Direction.NORTH);
                            Sprite extractedTop = extractSpriteFromQuads(boxGuiModel, Direction.UP);
                            if (extractedSide != null) sideSprite64 = extractedSide;
                            if (extractedTop != null) topSprite64 = extractedTop;
                            if (extractedSide == null && boxGuiModel.getParticleSprite() != null) {
                                sideSprite64 = boxGuiModel.getParticleSprite();
                                topSprite64 = sideSprite64;
                            }
                        }

                        BakedModel blockRampModel = new RampBakedModel(particleSprite, topSprite, model.getTransformation());
                        BakedModel guiRampModel = new RampBakedModel(sideSprite64, topSprite64, model.getTransformation());

                        finalModel = new ForwardingBakedModel() {
                            {
                                this.wrapped = blockRampModel;
                            }

                            @Override
                            public boolean isBuiltin() {
                                return false;
                            }

                            @Override
                            public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext renderContext) {
                                BakedModel itemModel = ContextAwareBakedModel.isGuiContext() ? guiRampModel : blockRampModel;
                                ((FabricBakedModel) itemModel).emitItemQuads(stack, randomSupplier, renderContext);
                            }

                            @Override
                            public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext renderContext) {
                                ((FabricBakedModel) blockRampModel).emitBlockQuads(blockView, state, pos, randomSupplier, renderContext);
                            }

                            @Override
                            public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
                                if (state == null) {
                                    return ContextAwareBakedModel.isGuiContext() ? guiRampModel.getQuads(null, face, random) : blockRampModel.getQuads(null, face, random);
                                }
                                return blockRampModel.getQuads(state, face, random);
                            }
                        };
                    }
                }

                Identifier guiModelId = ITEM_TO_GUI_MODEL_MAP.get(context.id());
                if (guiModelId != null) {
                    BakedModel guiModel = context.baker().bake(guiModelId, context.settings());
                    if (guiModel != null) {
                        return new ContextAwareBakedModel(finalModel, guiModel);
                    }
                }

                return finalModel;
            });
        });

        PlacementPreviewRenderer.register();

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldContext, hitResult) -> {
            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos pos = blockHitResult.getBlockPos();
                BlockState state = worldContext.world().getBlockState(pos);

                if (state.getBlock() instanceof RampBlock) {
                    MatrixStack matrices = worldContext.matrixStack();
                    Vec3d cameraPos = worldContext.camera().getPos();

                    matrices.push();
                    matrices.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);

                    VertexConsumer consumer = worldContext.consumers().getBuffer(RenderLayer.getLines());
                    EdgeOutlineRenderer.drawCustomRampOutline(matrices, consumer, state);

                    matrices.pop();
                    return false;
                }
            }
            return true;
        });
    }

    private static Sprite extractSpriteFromQuads(BakedModel model, Direction face) {
        List<BakedQuad> quads = model.getQuads(null, face, Random.create());
        if (quads != null && !quads.isEmpty()) {
            return quads.get(0).getSprite();
        }
        quads = model.getQuads(null, null, Random.create());
        if (quads != null && !quads.isEmpty()) {
            return quads.get(0).getSprite();
        }
        return null;
    }
}
