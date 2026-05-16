package me.maceaura.blockouttools.util;

import me.maceaura.blockouttools.data.BlockoutProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;


public class PlacementStateBuilder<T extends PlacementStateBuilder<T>> {

    protected final Block block;
    protected final ItemPlacementContext ctx;
    @Nullable
    protected BlockState state;

    protected PlacementStateBuilder(Block block, ItemPlacementContext ctx) {
        this.block = block;
        this.ctx = ctx;
        this.state = block.getDefaultState();
    }

    public static PlacementStateBuilder<?> of(Block block, ItemPlacementContext ctx) {
        return new PlacementStateBuilder<>(block, ctx);
    }


    public final T withHorizontalFacing() {
        return withHorizontalFacing(false);
    }


    public final T withHorizontalFacing(boolean opposite) {
        if (state == null) return self();

        Direction dir = ctx.getHorizontalPlayerFacing();
        if (opposite) {
            dir = dir.getOpposite();
        }
        state = state.with(BlockoutProperties.FACING_HOR, dir);
        return self();
    }


    public final T withTargetOrHorizontalFacing() {
        return withTargetOrHorizontalFacing(false);
    }

    public final T withTargetOrHorizontalFacing(boolean opposite) {
        if (state == null) return self();

        Direction face = ctx.getSide();
        if (!PlacementUtils.isY(face)) {
            if (!opposite) {
                face = face.getOpposite();
            }
            state = state.with(BlockoutProperties.FACING_HOR, face);
            return self();
        }
        return withHorizontalFacing(opposite);
    }


    public final T withHorizontalTargetFacing() {
        return withHorizontalTargetFacing(false);
    }

    public final T withHorizontalTargetFacing(boolean opposite) {
        if (state == null) return self();

        Direction face = ctx.getSide();
        if (PlacementUtils.isY(face)) {
            state = null;
            return self();
        }

        if (!opposite) {
            face = face.getOpposite();
        }
        state = state.with(BlockoutProperties.FACING_HOR, face);
        return self();
    }


    public final T withTargetFacing() {
        return withTargetFacing(false);
    }

    public final T withTargetFacing(boolean opposite) {
        if (state == null) return self();

        Direction face = ctx.getSide();
        if (!opposite) {
            face = face.getOpposite();
        }
        state = state.with(Properties.FACING, face);
        return self();
    }


    public final T withHalfOrHorizontalFacing() {
        if (state == null) return self();

        Direction side = ctx.getSide();
        if (PlacementUtils.isY(side)) {
            state = state.with(BlockoutProperties.FACING_HOR, ctx.getHorizontalPlayerFacing());
        } else if (PlacementUtils.fractionInDir(ctx.getHitPos(), side.rotateYCounterclockwise()) > .5) {
            state = state.with(BlockoutProperties.FACING_HOR, side.getOpposite().rotateYClockwise());
        } else {
            state = state.with(BlockoutProperties.FACING_HOR, side.getOpposite());
        }
        return self();
    }


    public final T withHalfOrQuarterFacing() {
        if (state == null) return self();

        Direction side = ctx.getSide();
        if (PlacementUtils.isY(side)) {
            Vec3d hitVec = ctx.getHitPos();
            double x = PlacementUtils.fractionInDir(hitVec, Direction.EAST);
            double z = PlacementUtils.fractionInDir(hitVec, Direction.SOUTH);

            Direction dir = z > .5D ? Direction.SOUTH : Direction.NORTH;
            if ((x > .5D) != PlacementUtils.isPositive(dir)) {
                dir = dir.rotateYClockwise();
            }
            state = state.with(BlockoutProperties.FACING_HOR, dir);

            return self();
        }
        return withHalfOrHorizontalFacing();
    }


    public final T withHalfFacing() {
        if (state == null) return self();

        Direction side = ctx.getSide();
        if (PlacementUtils.isY(side)) {
            Direction dir = ctx.getHorizontalPlayerFacing();
            double xz = PlacementUtils.fractionInDir(ctx.getHitPos(), dir.rotateYClockwise());
            if (xz > .5D) {
                dir = dir.rotateYClockwise();
            }
            state = state.with(BlockoutProperties.FACING_HOR, dir);

            return self();
        }
        return withHalfOrHorizontalFacing();
    }


    public final T withClickedAxis() {
        if (state != null) {
            state = state.with(Properties.AXIS, ctx.getSide().getAxis());
        }
        return self();
    }


    public final T withTop() {
        return withTop(BlockoutProperties.TOP);
    }

    public final T withTop(BooleanProperty prop) {
        if (state == null) return self();

        Direction side = ctx.getSide();
        if (side == Direction.DOWN) {
            state = state.with(prop, true);
        } else if (side == Direction.UP) {
            state = state.with(prop, false);
        } else {
            double y = PlacementUtils.fractionInDir(ctx.getHitPos(), Direction.UP);
            state = state.with(prop, y >= .5D);
        }
        return self();
    }


    public final T tryWithWater() {
        if (state != null && state.contains(Properties.WATERLOGGED)) {
            return withWater();
        }
        return self();
    }


    public final T withWater() {
        if (state == null) return self();

        state = state.with(Properties.WATERLOGGED,
                ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == Fluids.WATER);
        return self();
    }


    public final T withCustom(BiFunction<BlockState, ItemPlacementContext, @Nullable BlockState> modifier) {
        if (state == null) return self();

        state = modifier.apply(state, ctx);
        return self();
    }


    public final T validate(BiPredicate<BlockState, ItemPlacementContext> validator) {
        if (!validator.test(state, ctx)) {
            state = null;
        }
        return self();
    }

    @Nullable
    public final BlockState build() {
        return state;
    }

    @SuppressWarnings("unchecked")
    protected final T self() {
        return (T) this;
    }
}
