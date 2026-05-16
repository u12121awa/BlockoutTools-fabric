package me.maceaura.blockouttools.client.render;

import me.maceaura.blockouttools.block.impl.RampBlock;
import me.maceaura.blockouttools.data.property.SlopeType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class EdgeOutlineRenderer {
    public static void drawCustomRampOutline(MatrixStack matrices, VertexConsumer consumer, BlockState state) {
        Direction facing = state.get(RampBlock.FACING);
        SlopeType type = state.get(RampBlock.TYPE);

        Matrix4f posMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        if (type == SlopeType.BOTTOM) {
            drawBoxBottom(consumer, posMatrix, normalMatrix);
            switch (facing) {
                case SOUTH -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 1, 0, 0);
                }
                case EAST -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 0, 0, 1);
                }
                case WEST -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 1, 0, 1);
                }
                default -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 0, 1);
                }
            }
        } else if (type == SlopeType.TOP) {
            drawBoxTop(consumer, posMatrix, normalMatrix);
            switch (facing) {
                case SOUTH -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 1, 1, 0);
                }
                case EAST -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 0, 1, 1);
                }
                case WEST -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 1, 1, 1);
                }
                default -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 1, 1);
                }
            }
        } else if (type == SlopeType.HORIZONTAL) {
            switch (facing) {
                case SOUTH -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 1, 0, 1);
                }
                case EAST -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 1, 0, 0);
                }
                case WEST -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 0, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 0, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 0, 0, 1);
                }
                default -> {
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 0, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 1, 1, 1);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 1, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 0, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 1, 0, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 0, 1, 0);
                    drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 0, 0, 0);
                }
            }
        }
    }

    public static void drawEdge(VertexConsumer consumer, Matrix4f posMatrix, Matrix3f normalMatrix, float x1, float y1, float z1, float x2, float y2, float z2) {
        consumer.vertex(posMatrix, x1, y1, z1).color(0f, 0f, 0f, 0.4f).normal(normalMatrix, x2 - x1, y2 - y1, z2 - z1).next();
        consumer.vertex(posMatrix, x2, y2, z2).color(0f, 0f, 0f, 0.4f).normal(normalMatrix, x2 - x1, y2 - y1, z2 - z1).next();
    }

    public static void drawBoxBottom(VertexConsumer consumer, Matrix4f posMatrix, Matrix3f normalMatrix) {
        drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 0, 1, 0, 0);
        drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 0, 1, 0, 1);
        drawEdge(consumer, posMatrix, normalMatrix, 1, 0, 1, 0, 0, 1);
        drawEdge(consumer, posMatrix, normalMatrix, 0, 0, 1, 0, 0, 0);
    }

    public static void drawBoxTop(VertexConsumer consumer, Matrix4f posMatrix, Matrix3f normalMatrix) {
        drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 0, 1, 1, 0);
        drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 0, 1, 1, 1);
        drawEdge(consumer, posMatrix, normalMatrix, 1, 1, 1, 0, 1, 1);
        drawEdge(consumer, posMatrix, normalMatrix, 0, 1, 1, 0, 1, 0);
    }
}
