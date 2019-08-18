package grondag.fermion.spatial;

import static net.minecraft.util.math.Direction.*;

import net.minecraft.util.math.Direction;

/**
 * Based on vanilla methods but reliably public and available server-side.
 */
public class DirectionHelper {
    private DirectionHelper() {}
    
    public static Direction clockwise(Direction face, Direction.Axis axis) {
       switch(axis) {
       case X:
          if (face != WEST && face != EAST) {
             return rotateXClockwise(face);
          }

          return face;
       case Y:
          if (face != UP && face != DOWN) {
             return rotateYClockwise(face);
          }

          return face;
       case Z:
          if (face != NORTH && face != SOUTH) {
             return rotateZClockwise(face);
          }

          return face;
       default:
          throw new IllegalStateException("Unable to get CW facing for axis " + axis);
       }
    }

    public static Direction counterClockwise(Direction face, Direction.Axis axis) {
        return clockwise(face.getOpposite(), axis);
     }
    
    public static Direction rotateYClockwise(Direction face) {
       switch(face) {
       case NORTH:
          return EAST;
       case EAST:
          return SOUTH;
       case SOUTH:
          return WEST;
       case WEST:
          return NORTH;
       default:
          throw new IllegalStateException("Unable to get Y-rotated facing of " + face);
       }
    }

    public static Direction rotateXClockwise(Direction face) {
       switch(face) {
       case NORTH:
          return DOWN;
       case EAST:
       case WEST:
       default:
          throw new IllegalStateException("Unable to get X-rotated facing of " + face);
       case SOUTH:
          return UP;
       case UP:
          return NORTH;
       case DOWN:
          return SOUTH;
       }
    }

    public static Direction rotateZClockwise(Direction face) {
       switch(face) {
       case EAST:
          return DOWN;
       case SOUTH:
       default:
          throw new IllegalStateException("Unable to get Z-rotated facing of " + face);
       case WEST:
          return UP;
       case UP:
          return EAST;
       case DOWN:
          return WEST;
       }
    }

    public static Direction rotateYCounterclockwise(Direction face) {
       switch(face) {
       case NORTH:
          return WEST;
       case EAST:
          return NORTH;
       case SOUTH:
          return EAST;
       case WEST:
          return SOUTH;
       default:
          throw new IllegalStateException("Unable to get CCW facing of " + face);
       }
    }
}
