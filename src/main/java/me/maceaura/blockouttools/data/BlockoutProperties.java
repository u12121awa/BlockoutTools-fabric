package me.maceaura.blockouttools.data;

import me.maceaura.blockouttools.data.property.CornerType;
import me.maceaura.blockouttools.data.property.HorizontalRotation;
import me.maceaura.blockouttools.data.property.SlopeType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public final class BlockoutProperties {

    public static final DirectionProperty FACING_HOR = Properties.HORIZONTAL_FACING;

    public static final BooleanProperty TOP = BooleanProperty.of("top");
    public static final BooleanProperty RIGHT = BooleanProperty.of("right");
    public static final BooleanProperty FRONT = BooleanProperty.of("front");

    public static final EnumProperty<SlopeType> SLOPE_TYPE = EnumProperty.of("type", SlopeType.class);
    public static final EnumProperty<CornerType> CORNER_TYPE = EnumProperty.of("type", CornerType.class);
    public static final EnumProperty<HorizontalRotation> ROTATION = EnumProperty.of("rotation", HorizontalRotation.class);

    private BlockoutProperties() {}
}
