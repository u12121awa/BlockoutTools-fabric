package me.maceaura.blockouttools.data.property;

import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.Locale;

public enum CornerType implements StringIdentifiable {
    BOTTOM,
    TOP,
    HORIZONTAL_BOTTOM_LEFT,
    HORIZONTAL_BOTTOM_RIGHT,
    HORIZONTAL_TOP_LEFT,
    HORIZONTAL_TOP_RIGHT;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String asString() {
        return name;
    }

    public boolean isHorizontal() {
        return this != BOTTOM && this != TOP;
    }

    public boolean isTop() {
        return this == TOP || this == HORIZONTAL_TOP_LEFT || this == HORIZONTAL_TOP_RIGHT;
    }

    public boolean isRight() {
        return this == HORIZONTAL_BOTTOM_RIGHT || this == HORIZONTAL_TOP_RIGHT;
    }

    public CornerType verticalOpposite() {
        switch (this) {
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            case HORIZONTAL_BOTTOM_RIGHT:
                return HORIZONTAL_TOP_RIGHT;
            case HORIZONTAL_BOTTOM_LEFT:
                return HORIZONTAL_TOP_LEFT;
            case HORIZONTAL_TOP_RIGHT:
                return HORIZONTAL_BOTTOM_RIGHT;
            case HORIZONTAL_TOP_LEFT:
                return HORIZONTAL_BOTTOM_LEFT;
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    public CornerType horizontalOpposite() {
        switch (this) {
            case TOP:
            case BOTTOM:
                return this;
            case HORIZONTAL_BOTTOM_RIGHT:
                return HORIZONTAL_BOTTOM_LEFT;
            case HORIZONTAL_BOTTOM_LEFT:
                return HORIZONTAL_BOTTOM_RIGHT;
            case HORIZONTAL_TOP_RIGHT:
                return HORIZONTAL_TOP_LEFT;
            case HORIZONTAL_TOP_LEFT:
                return HORIZONTAL_TOP_RIGHT;
            default:
                throw new IllegalStateException("Unreachable");
        }
    }

    public CornerType rotate(BlockRotation rot) {
        switch (this) {
            case HORIZONTAL_TOP_LEFT:
                switch (rot) {
                    case NONE:
                        return this;
                    case CLOCKWISE_90:
                        return HORIZONTAL_TOP_RIGHT;
                    case CLOCKWISE_180:
                        return HORIZONTAL_BOTTOM_RIGHT;
                    case COUNTERCLOCKWISE_90:
                        return HORIZONTAL_BOTTOM_LEFT;
                }
                break;
            case HORIZONTAL_TOP_RIGHT:
                switch (rot) {
                    case NONE:
                        return this;
                    case CLOCKWISE_90:
                        return HORIZONTAL_BOTTOM_RIGHT;
                    case CLOCKWISE_180:
                        return HORIZONTAL_BOTTOM_LEFT;
                    case COUNTERCLOCKWISE_90:
                        return HORIZONTAL_TOP_LEFT;
                }
                break;
            case HORIZONTAL_BOTTOM_LEFT:
                switch (rot) {
                    case NONE:
                        return this;
                    case CLOCKWISE_90:
                        return HORIZONTAL_TOP_LEFT;
                    case CLOCKWISE_180:
                        return HORIZONTAL_TOP_RIGHT;
                    case COUNTERCLOCKWISE_90:
                        return HORIZONTAL_BOTTOM_RIGHT;
                }
                break;
            case HORIZONTAL_BOTTOM_RIGHT:
                switch (rot) {
                    case NONE:
                        return this;
                    case CLOCKWISE_90:
                        return HORIZONTAL_BOTTOM_LEFT;
                    case CLOCKWISE_180:
                        return HORIZONTAL_TOP_LEFT;
                    case COUNTERCLOCKWISE_90:
                        return HORIZONTAL_TOP_RIGHT;
                }
                break;
            case TOP:
            case BOTTOM:
                throw new IllegalStateException("Non-horizontal CornerTypes cannot be rotated");
        }
        throw new IllegalStateException("Unreachable");
    }

    public boolean isHorizontalAdjacent(Direction dir, Direction side, CornerType adjType) {
        if (!isHorizontal() || !adjType.isHorizontal()) {
            return false;
        }

        switch (this) {
            case HORIZONTAL_TOP_LEFT:
                if (side == Direction.UP) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                if (side == dir.rotateYCounterclockwise()) { return adjType == HORIZONTAL_TOP_RIGHT; }
                return false;
            case HORIZONTAL_TOP_RIGHT:
                if (side == Direction.UP) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                if (side == dir.rotateYClockwise()) { return adjType == HORIZONTAL_TOP_LEFT; }
                return false;
            case HORIZONTAL_BOTTOM_LEFT:
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_TOP_LEFT; }
                if (side == dir.rotateYCounterclockwise()) { return adjType == HORIZONTAL_BOTTOM_RIGHT; }
                return false;
            case HORIZONTAL_BOTTOM_RIGHT:
                if (side == Direction.DOWN) { return adjType == HORIZONTAL_TOP_RIGHT; }
                if (side == dir.rotateYClockwise()) { return adjType == HORIZONTAL_BOTTOM_LEFT; }
                return false;
            default:
                return false;
        }
    }
}
