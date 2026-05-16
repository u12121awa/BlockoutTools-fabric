package me.maceaura.blockouttools.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class PlacementUtils {

    public static Vec3d fraction(Vec3d vec) {
        return new Vec3d(
                vec.x - Math.floor(vec.x),
                vec.y - Math.floor(vec.y),
                vec.z - Math.floor(vec.z)
        );
    }

    public static double fractionInDir(Vec3d vec, Direction dir) {
        double coord;
        switch (dir.getAxis()) {
            case X:
                coord = vec.x;
                break;
            case Y:
                coord = vec.y;
                break;
            case Z:
                coord = vec.z;
                break;
            default:
                coord = 0;
        }
        coord = coord - Math.floor(coord);
        return isPositive(dir) ? coord : (1D - coord);
    }

    public static boolean isPositive(Direction dir) {
        return dir.getDirection() == Direction.AxisDirection.POSITIVE;
    }

    public static boolean isX(Direction dir) {
        return dir.getAxis() == Direction.Axis.X;
    }

    public static boolean isY(Direction dir) {
        return dir.getAxis() == Direction.Axis.Y;
    }

    public static boolean isZ(Direction dir) {
        return dir.getAxis() == Direction.Axis.Z;
    }

    private PlacementUtils() {}
}
