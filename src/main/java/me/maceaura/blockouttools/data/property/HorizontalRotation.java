package me.maceaura.blockouttools.data.property;

import me.maceaura.blockouttools.util.PlacementUtils;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;
import java.util.function.Function;

public enum HorizontalRotation implements StringIdentifiable {
    UP(dir -> Direction.UP),
    DOWN(dir -> Direction.DOWN),
    RIGHT(Direction::rotateYClockwise),
    LEFT(Direction::rotateYCounterclockwise);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Function<Direction, Direction> facingMod;

    HorizontalRotation(Function<Direction, Direction> facingMod) {
        this.facingMod = facingMod;
    }

    public Direction withFacing(Direction dir) {
        return facingMod.apply(dir);
    }

    public HorizontalRotation getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case RIGHT:
                return LEFT;
            case LEFT:
                return RIGHT;
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    public HorizontalRotation rotate(BlockRotation rot) {
        switch (rot) {
            case NONE:
                return this;
            case CLOCKWISE_180:
                return getOpposite();
            case CLOCKWISE_90:
                switch (this) {
                    case UP:
                        return RIGHT;
                    case DOWN:
                        return LEFT;
                    case RIGHT:
                        return DOWN;
                    case LEFT:
                        return UP;
                    default:
                        throw new IllegalStateException("Unreachable");
                }
            case COUNTERCLOCKWISE_90:
                switch (this) {
                    case UP:
                        return LEFT;
                    case DOWN:
                        return RIGHT;
                    case RIGHT:
                        return UP;
                    case LEFT:
                        return DOWN;
                    default:
                        throw new IllegalStateException("Unreachable");
                }
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }

    public boolean isSameDir(Direction dir, HorizontalRotation adjRot, Direction adjDir) {
        return withFacing(dir) == adjRot.withFacing(adjDir);
    }

    @Override
    public String asString() {
        return name;
    }

    public static HorizontalRotation fromDirection(Direction facing, Direction dir) {
        if (PlacementUtils.isY(facing)) {
            throw new IllegalArgumentException("View direction must not be on the Y axis");
        }
        if (facing.getAxis() == dir.getAxis()) {
            throw new IllegalArgumentException("Directions must be perpendicular");
        }

        if (dir == Direction.UP) { return UP; }
        if (dir == Direction.DOWN) { return DOWN; }
        if (dir == facing.rotateYClockwise()) { return RIGHT; }
        if (dir == facing.rotateYCounterclockwise()) { return LEFT; }
        throw new IllegalStateException(String.format("How did we get here?! %s|%s", facing, dir));
    }


    public static HorizontalRotation fromDirection(Direction facing, Direction dir, Vec3d hitVec) {
        HorizontalRotation rot = fromDirection(facing, dir);
        double dist;
        switch (rot) {
            case UP:
                dist = PlacementUtils.fractionInDir(hitVec, facing.rotateYCounterclockwise());
                break;
            case DOWN:
                dist = PlacementUtils.fractionInDir(hitVec, facing.rotateYClockwise());
                break;
            case RIGHT:
                dist = PlacementUtils.fractionInDir(hitVec, Direction.UP);
                break;
            case LEFT:
                dist = PlacementUtils.fractionInDir(hitVec, Direction.DOWN);
                break;
            default:
                dist = 0;
        }
        return dist > .5 ? rot.rotate(BlockRotation.CLOCKWISE_90) : rot;
    }


    public static HorizontalRotation fromWallCross(Vec3d hitVec, Direction hitFace) {
        hitVec = PlacementUtils.fraction(hitVec);

        double xz = (PlacementUtils.isX(hitFace) ? hitVec.z : hitVec.x) - .5;
        double y = hitVec.y - .5;

        if (Math.max(Math.abs(xz), Math.abs(y)) == Math.abs(xz)) {
            if (PlacementUtils.isX(hitFace)) {
                return (xz < 0) == PlacementUtils.isPositive(hitFace) ? LEFT : RIGHT;
            } else {
                return (xz < 0) == PlacementUtils.isPositive(hitFace) ? RIGHT : LEFT;
            }
        } else {
            return y < 0 ? HorizontalRotation.UP : HorizontalRotation.DOWN;
        }
    }


    public static HorizontalRotation fromWallCorner(Vec3d hitVec, Direction hitFace) {
        if (PlacementUtils.isY(hitFace)) {
            throw new IllegalArgumentException("Hit face must not be on the Y axis");
        }

        hitVec = PlacementUtils.fraction(hitVec);

        double xz = (PlacementUtils.isX(hitFace) ? hitVec.z : hitVec.x);
        if (!PlacementUtils.isPositive(hitFace.rotateYCounterclockwise())) {
            xz = 1D - xz;
        }

        if (hitVec.y > .5D) {
            return xz > .5D ? LEFT : DOWN;
        } else {
            return xz > .5D ? UP : RIGHT;
        }
    }
}
