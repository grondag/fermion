package grondag.fermion.world;

import net.minecraft.util.math.Direction;

/**
 * Texture rotations. Used mainly when rotated textures are used as alternate
 * textures.
 */
public enum Rotation {
    ROTATE_NONE(0, Direction.NORTH), ROTATE_90(90, Direction.EAST), ROTATE_180(180, Direction.SOUTH),
    ROTATE_270(270, Direction.WEST);

    public static final Rotation[] VALUES = Rotation.values();
    public static final int COUNT = VALUES.length;

    /**
     * Useful for locating model file names that use degrees as a suffix.
     */
    public final int degrees;

    /**
     * Opposite of degress - useful for GL transforms. 0 and 180 are same, 90 and
     * 270 are flipped
     */
    public final int degreesInverse;

    /**
     * Horizontal face that corresponds to this rotation for SuperBlocks that have a
     * single rotated face.
     */
    public final Direction horizontalFace;

    private static Rotation[] FROM_HORIZONTAL_FACING = new Rotation[6];

    static {
        FROM_HORIZONTAL_FACING[Direction.NORTH.ordinal()] = ROTATE_NONE;
        FROM_HORIZONTAL_FACING[Direction.EAST.ordinal()] = ROTATE_90;
        FROM_HORIZONTAL_FACING[Direction.SOUTH.ordinal()] = ROTATE_180;
        FROM_HORIZONTAL_FACING[Direction.WEST.ordinal()] = ROTATE_270;
        FROM_HORIZONTAL_FACING[Direction.NORTH.ordinal()] = ROTATE_NONE;
        FROM_HORIZONTAL_FACING[Direction.NORTH.ordinal()] = ROTATE_NONE;
    }

    Rotation(int degrees, Direction horizontalFace) {
        this.degrees = degrees;
        this.degreesInverse = (360 - degrees) % 360;
        this.horizontalFace = horizontalFace;

    }

    public Rotation clockwise() {
        switch (this) {
        case ROTATE_180:
            return ROTATE_270;
        case ROTATE_270:
            return ROTATE_NONE;
        case ROTATE_90:
            return ROTATE_180;
        case ROTATE_NONE:
        default:
            return ROTATE_90;
        }
    }

    /**
     * Gives the rotation with horiztonalFace matching the given NSEW face For up
     * and down will return ROTATE_NONE
     */
    public static Rotation fromHorizontalFacing(Direction face) {
        return FROM_HORIZONTAL_FACING[face.ordinal()];
    }

}