package me.maceaura.blockouttools.util;

import me.maceaura.blockouttools.data.BlockoutProperties;
import me.maceaura.blockouttools.data.property.CornerType;
import me.maceaura.blockouttools.data.property.HorizontalRotation;
import me.maceaura.blockouttools.data.property.SlopeType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class BlockoutPlacementStateBuilder extends PlacementStateBuilder<BlockoutPlacementStateBuilder> {

    private BlockoutPlacementStateBuilder(Block block, ItemPlacementContext ctx) {
        super(block, ctx);
    }

    public static BlockoutPlacementStateBuilder of(Block block, ItemPlacementContext ctx) {
        return new BlockoutPlacementStateBuilder(block, ctx);
    }

    public BlockoutPlacementStateBuilder withRight() {
        if (state == null) return self();

        boolean right = PlacementUtils.fractionInDir(ctx.getHitPos(), ctx.getHorizontalPlayerFacing().rotateYClockwise()) > .5D;
        state = state.with(BlockoutProperties.RIGHT, right);
        return self();
    }


    public BlockoutPlacementStateBuilder withHorizontalFacingAndSlopeType() {
        if (state == null) return self();

        Direction side = ctx.getSide();
        Direction facing = ctx.getHorizontalPlayerFacing();

        state = state.with(BlockoutProperties.FACING_HOR, facing);

        if (!PlacementUtils.isY(side)) {
            double y = PlacementUtils.fractionInDir(ctx.getHitPos(), Direction.UP);
            if (y < (3D / 16D)) {
                side = Direction.UP;
            } else if (y > (13D / 16D)) {
                side = Direction.DOWN;
            }
        }

        if (side == Direction.DOWN) {
            state = state.with(BlockoutProperties.SLOPE_TYPE, SlopeType.TOP);
        } else if (side == Direction.UP) {
            state = state.with(BlockoutProperties.SLOPE_TYPE, SlopeType.BOTTOM);
        } else {
            state = state.with(BlockoutProperties.SLOPE_TYPE, SlopeType.HORIZONTAL);

            boolean isRightHalf = PlacementUtils.fractionInDir(ctx.getHitPos(), facing.rotateYClockwise()) > 0.5D;

            Direction placementFacing;
            if (isRightHalf) {
                placementFacing = facing;
            } else {
                placementFacing = facing.rotateYCounterclockwise();
            }

            state = state.with(BlockoutProperties.FACING_HOR, placementFacing);
        }

        return self();
    }

    public BlockoutPlacementStateBuilder withHorizontalFacingAndCornerType() {
        if (state == null) return self();

        Direction side = ctx.getSide();
        Vec3d hitPoint = PlacementUtils.fraction(ctx.getHitPos());

        Direction typeSide = side;
        if (!PlacementUtils.isY(side)) {
            if (hitPoint.y < (3D / 16D)) {
                typeSide = Direction.UP;
            } else if (hitPoint.y > (13D / 16D)) {
                typeSide = Direction.DOWN;
            }
        }

        if (typeSide == Direction.DOWN) {
            state = state.with(BlockoutProperties.CORNER_TYPE, CornerType.TOP);
            withHalfFacing();
        } else if (typeSide == Direction.UP) {
            state = state.with(BlockoutProperties.CORNER_TYPE, CornerType.BOTTOM);
            withHalfFacing();
        } else {
            boolean xAxis = PlacementUtils.isX(side);
            boolean positive = PlacementUtils.isPositive(side.rotateYCounterclockwise());
            double xz = xAxis ? hitPoint.z : hitPoint.x;
            double y = hitPoint.y;

            CornerType type;
            if ((xz > .5D) == positive) {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            } else {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }

            state = state.with(BlockoutProperties.CORNER_TYPE, type)
                    .with(BlockoutProperties.FACING_HOR, ctx.getHorizontalPlayerFacing());
        }

        return self();
    }


    public BlockoutPlacementStateBuilder withCornerOrSideRotation() {
        return withCornerOrSideRotation(false);
    }

    public BlockoutPlacementStateBuilder withCornerOrSideRotation(boolean opposite) {
        if (state == null) return self();

        Direction facing = ctx.getHorizontalPlayerFacing();
        Direction side = ctx.getSide();
        if (side == facing) {
            state = null;
            return self();
        }

        HorizontalRotation rotation;
        if (side == facing.getOpposite()) {
            rotation = HorizontalRotation.fromWallCorner(ctx.getHitPos(), side);
        } else {
            rotation = HorizontalRotation.fromDirection(facing, side, ctx.getHitPos());
        }
        if (opposite) {
            rotation = rotation.getOpposite();
        }
        state = state.with(BlockoutProperties.ROTATION, rotation);

        return self();
    }

    public BlockoutPlacementStateBuilder withCrossOrSideRotation() {
        return withCrossOrSideRotation(false);
    }

    public BlockoutPlacementStateBuilder withCrossOrSideRotation(boolean opposite) {
        if (state == null) return self();

        Direction facing = ctx.getHorizontalPlayerFacing();
        Direction side = ctx.getSide();
        if (side == facing) {
            state = null;
            return self();
        }

        HorizontalRotation rotation;
        if (side == facing.getOpposite()) {
            rotation = HorizontalRotation.fromWallCross(ctx.getHitPos(), side);
        } else {
            rotation = HorizontalRotation.fromDirection(facing, side);
        }
        if (opposite) {
            rotation = rotation.getOpposite();
        }
        state = state.with(BlockoutProperties.ROTATION, rotation);

        return self();
    }

    public BlockoutPlacementStateBuilder withCornerRotation() {
        return withCornerRotation(false);
    }

    public BlockoutPlacementStateBuilder withCornerRotation(boolean opposite) {
        if (state == null) return self();

        HorizontalRotation rotation = HorizontalRotation.fromWallCorner(ctx.getHitPos(), ctx.getSide());
        if (opposite) {
            rotation = rotation.getOpposite();
        }
        state = state.with(BlockoutProperties.ROTATION, rotation);

        return self();
    }

    public BlockoutPlacementStateBuilder withCrossRotation() {
        return withCrossRotation(false);
    }

    public BlockoutPlacementStateBuilder withCrossRotation(boolean opposite) {
        if (state == null) return self();

        HorizontalRotation rotation = HorizontalRotation.fromWallCross(ctx.getHitPos(), ctx.getSide());
        if (opposite) {
            rotation = rotation.getOpposite();
        }
        state = state.with(BlockoutProperties.ROTATION, rotation);

        return self();
    }


    public BlockoutPlacementStateBuilder withFront() {
        if (state == null) return self();

        Direction facing = ctx.getHorizontalPlayerFacing();
        if (facing.getAxis() != ctx.getSide().getAxis()) {
            double xz = PlacementUtils.fractionInDir(ctx.getHitPos(), facing);
            state = state.with(BlockoutProperties.FRONT, xz < .5);
        }

        return self();
    }

    public BlockoutPlacementStateBuilder withDirectionalSlabFacing() {
        if (state == null) return self();

        Direction clickedFace = ctx.getSide();
        Direction facing;

        if (clickedFace == Direction.UP) {
            facing = Direction.DOWN;
        } else if (clickedFace == Direction.DOWN) {
            facing = Direction.UP;
        } else {
            double y = PlacementUtils.fractionInDir(ctx.getHitPos(), Direction.UP);
            if (y <= 0.25) {
                facing = Direction.DOWN;
            } else if (y >= 0.75) {
                facing = Direction.UP;
            } else {
                facing = clickedFace.getOpposite();
            }
        }

        state = state.with(Properties.FACING, facing);
        return self();
    }
}
