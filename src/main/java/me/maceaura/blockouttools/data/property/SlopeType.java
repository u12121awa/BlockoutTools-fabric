package me.maceaura.blockouttools.data.property;

import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum SlopeType implements StringIdentifiable {
    BOTTOM,
    HORIZONTAL,
    TOP;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String asString() {
        return name;
    }

    public SlopeType getOpposite() {
        switch (this) {
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            default:
                throw new IllegalArgumentException("Can't get opposite of '" + asString() + "'!");
        }
    }
}
