package me.maceaura.blockouttools.client.render;

import me.maceaura.blockouttools.block.BlockoutToolsBlock;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;


public class PlacementPreviewRenderer {
    private static final int GHOST_ALPHA = 0x88;
    private static final float SCALE = 1.001F;

    public static void register() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(PlacementPreviewRenderer::onBeforeBlockOutline);
    }

    private static boolean onBeforeBlockOutline(WorldRenderContext context, HitResult hitResult) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return true;
        }

        ItemStack heldItem = client.player.getMainHandStack();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof BlockItem)) {
            heldItem = client.player.getOffHandStack();
            if (heldItem.isEmpty() || !(heldItem.getItem() instanceof BlockItem)) {
                return true;
            }
        }

        BlockItem blockItem = (BlockItem) heldItem.getItem();
        Block block = blockItem.getBlock();

        if (!(block instanceof BlockoutToolsBlock)) return true;


        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return true;
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        Direction side = blockHit.getSide();
        BlockPos placePos = blockHit.getBlockPos().offset(side);

        World world = client.world;
        if (!world.getBlockState(placePos).isReplaceable()) {
            return true;
        }

        for (Entity entity : new ArrayList<>(client.world.getNonSpectatingEntities(Entity.class, new Box(placePos))))
            if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity))
                return true;

        BlockState previewState = computePreviewState(block, blockHit, client);
        if (previewState == null) {
            return true;
        }

        renderGhostBlock(context, previewState, placePos, client);

        return true;
    }

    private static BlockState computePreviewState(Block block, BlockHitResult blockHit, MinecraftClient client) {
        if (client.player == null) {
            return null;
        }

        ItemStack stack = client.player.getMainHandStack();
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) {
            stack = client.player.getOffHandStack();
        }

        ItemPlacementContext ctx = new ItemPlacementContext(client.player, Hand.MAIN_HAND, stack, blockHit);
        return block.getPlacementState(ctx);
    }

    private static void renderGhostBlock(WorldRenderContext context, BlockState state, BlockPos pos, MinecraftClient client) {
        MatrixStack matrices = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();

        double offsetX = pos.getX() - cameraPos.x;
        double offsetY = pos.getY() - cameraPos.y;
        double offsetZ = pos.getZ() - cameraPos.z;

        matrices.push();

        matrices.translate(offsetX + 0.5, offsetY + 0.5, offsetZ + 0.5);
        matrices.scale(SCALE, SCALE, SCALE);
        matrices.translate(-0.5, -0.5, -0.5);

        BlockRenderManager blockRenderer = client.getBlockRenderManager();

        VertexConsumerProvider consumers = context.consumers();
        if (consumers != null) {
            VertexConsumer rawConsumer = consumers.getBuffer(RenderLayer.getTranslucent());
            VertexConsumer ghostConsumer = new GhostVertexConsumer(rawConsumer, GHOST_ALPHA);

            blockRenderer.renderBlock(
                    state,
                    pos,
                    client.world,
                    matrices,
                    ghostConsumer,
                    false,
                    client.world.getRandom()
            );
        }

        matrices.pop();
    }

}
