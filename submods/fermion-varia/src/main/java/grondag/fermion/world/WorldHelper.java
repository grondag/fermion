/*******************************************************************************
 * Copyright 2019 grondag
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package grondag.fermion.world;

import org.apache.commons.lang3.tuple.Pair;

import grondag.fermion.varia.Useful;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class WorldHelper {

    public static int horizontalDistanceSquared(BlockPos pos1, BlockPos pos2) {
        return Useful.squared(pos1.getX() - pos2.getX()) + Useful.squared(pos1.getZ() - pos2.getZ());
    }

    /**
     * Sorts members of the BlockPos vector so that x is largest and z is smallest.
     * Useful when BlockPos represents a volume instead of a position.
     */
    public static BlockPos sortedBlockPos(BlockPos pos) {

        if (pos.getX() > pos.getY()) {
            if (pos.getY() > pos.getZ()) {
                // x > y > z
                return pos;
            } else if (pos.getX() > pos.getZ()) {
                // x > z > y
                return new BlockPos(pos.getX(), pos.getZ(), pos.getY());
            } else {
                // z > x > y
                return new BlockPos(pos.getZ(), pos.getX(), pos.getY());
            }
        } else if (pos.getX() > pos.getZ()) {
            // y > x > z
            return new BlockPos(pos.getY(), pos.getX(), pos.getY());
        } else if (pos.getY() > pos.getZ()) {
            // y > z > x
            return new BlockPos(pos.getY(), pos.getZ(), pos.getX());
        } else {
            // z > y >x
            return new BlockPos(pos.getZ(), pos.getY(), pos.getX());
        }
    }

    public static Direction closestAdjacentFace(Direction hitFace, double hitX, double hitY, double hitZ) {
        return closestAdjacentFace(hitFace, (float)hitX, (float)hitY, (float)hitZ);
    }
    
    /**
     * Returns the closest face adjacent to the hit face that is closest to the hit
     * location on the given face. There is probably a better way to do this. TBH, I
     * may have been drinking when this code was written.
     */
    public static Direction closestAdjacentFace(Direction hitFace, float hitX, float hitY, float hitZ) {
        switch (hitFace.getAxis()) {
        case X: {
            // absolute distance from center of the face along the orthogonalAxis
            float yDist = 0.5F - hitY + MathHelper.floor(hitY);
            float zDist = 0.5F - hitZ + MathHelper.floor(hitZ);
            if (Math.abs(yDist) > Math.abs(zDist)) {
                return yDist < 0 ? Direction.UP : Direction.DOWN;
            } else {
                return zDist < 0 ? Direction.SOUTH : Direction.NORTH;
            }
        }

        case Y: {
            // absolute distance from center of the face along the orthogonalAxis
            float xDist = 0.5F - hitX + MathHelper.floor(hitX);
            float zDist = 0.5F - hitZ + MathHelper.floor(hitZ);
            if (Math.abs(xDist) > Math.abs(zDist)) {
                return xDist < 0 ? Direction.EAST : Direction.WEST;
            } else {
                return zDist < 0 ? Direction.SOUTH : Direction.NORTH;
            }
        }

        case Z: {
            // absolute distance from center of the face along the orthogonalAxis
            float yDist = 0.5F - hitY + MathHelper.floor(hitY);
            float xDist = 0.5F - hitX + MathHelper.floor(hitX);
            if (Math.abs(yDist) > Math.abs(xDist)) {
                return yDist < 0 ? Direction.UP : Direction.DOWN;
            } else {
                return xDist < 0 ? Direction.EAST : Direction.WEST;
            }
        }
        default:
            // whatever
            return hitFace.rotateYClockwise();
        }
    }
    
    public static Pair<Direction, Direction> closestAdjacentFaces(Direction hitFace, double hitX, double hitY, double hitZ) {
        return closestAdjacentFaces(hitFace, (float)hitX, (float)hitY, (float)hitZ);
    }

    /**
     * Returns the faces adjacent to the hit face that are closest to the hit
     * location on the given face. First item in pair is closest, and second is...
     * you know. These faces will necessarily be adjacent to each other.
     * <p>
     * 
     * Logic here is adapted from {@link closestAdjacentFace} except that I was
     * completely sober.
     */
    // PERF: return a CubeEdge instead of a pair
    public static Pair<Direction, Direction> closestAdjacentFaces(Direction hitFace, float hitX, float hitY,
            float hitZ) {
        switch (hitFace.getAxis()) {
        case X: {
            // absolute distance from center of the face along the orthogonalAxis
            float yDist = 0.5F - hitY + MathHelper.floor(hitY);
            float zDist = 0.5F - hitZ + MathHelper.floor(hitZ);
            Direction yFace = yDist < 0 ? Direction.UP : Direction.DOWN;
            Direction zFace = zDist < 0 ? Direction.SOUTH : Direction.NORTH;
            return Math.abs(yDist) > Math.abs(zDist) ? Pair.of(yFace, zFace) : Pair.of(zFace, yFace);
        }

        case Y: {
            // absolute distance from center of the face along the orthogonalAxis
            float xDist = 0.5F - hitX + MathHelper.floor(hitX);
            float zDist = 0.5F - hitZ + MathHelper.floor(hitZ);
            Direction xFace = xDist < 0 ? Direction.EAST : Direction.WEST;
            Direction zFace = zDist < 0 ? Direction.SOUTH : Direction.NORTH;
            return Math.abs(xDist) > Math.abs(zDist) ? Pair.of(xFace, zFace) : Pair.of(zFace, xFace);
        }

        default: // can't happen, just making compiler shut up
        case Z: {
            // absolute distance from center of the face along the orthogonalAxis
            float yDist = 0.5F - hitY + MathHelper.floor(hitY);
            float xDist = 0.5F - hitX + MathHelper.floor(hitX);
            Direction xFace = xDist < 0 ? Direction.EAST : Direction.WEST;
            Direction yFace = yDist < 0 ? Direction.UP : Direction.DOWN;
            return Math.abs(xDist) > Math.abs(yDist) ? Pair.of(xFace, yFace) : Pair.of(yFace, xFace);
        }
        }
    }
    
    /**
     * The direction that would appear as "up" adjusted if looking at an UP or DOWN
     * face. For example, if lookup up at the ceiling and facing North, then South
     * would be "up." For horizontal faces, is always real up.
     */
    public static Direction relativeUp(PlayerEntity player, Direction onFace) {
        switch (onFace) {
        case DOWN:
            return player.getHorizontalFacing();

        case UP:
            return player.getHorizontalFacing().getOpposite();

        default:
            return Direction.UP;

        }
    }

    /**
     * The direction that would appear as "left" adjusted if looking at an UP or
     * DOWN face. For example, if lookup up at the ceiling and facing North, then
     * West would be "left." For horizontal faces, is always direction left of the
     * direction <em>opposite</em> of the given face. (Player is looking at the
     * face, not away from it.)
     */
    public static Direction relativeLeft(PlayerEntity player, Direction onFace) {
        switch (onFace) {
        case DOWN:
            return Direction.fromHorizontal((relativeUp(player, onFace).getHorizontal() + 1) & 0x3).getOpposite();

        case UP:
            return Direction.fromHorizontal((relativeUp(player, onFace).getHorizontal() + 1) & 0x3);

        default:
            return Direction.fromHorizontal((player.getHorizontalFacing().getHorizontal() + 1) & 0x3).getOpposite();

        }
    }

//    /**
//     * Convenience method to keep code more readable.
//     * Call with replaceVirtualBlocks = true to behave as if virtual blocks not present.
//     * Should generally be true if placing a normal block.
//     */
//    public static boolean isBlockReplaceable(BlockView worldIn, BlockPos pos, boolean replaceVirtualBlocks)
//    {
//        if(replaceVirtualBlocks)
//        {
//            return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
//        }
//        else
//        {
//            Block block = worldIn.getBlockState(pos).getBlock();
//            return !ISuperBlock.isVirtualBlock(block) && block.isReplaceable(worldIn, pos);
//        }
//        
//    }

    public static boolean isOnRenderChunkBoundary(BlockPos pos) {
        int n = pos.getX() & 0xF;
        if (n == 0 || n == 0xF)
            return true;

        n = pos.getY() & 0xF;
        if (n == 0 || n == 0xF)
            return true;

        n = pos.getZ() & 0xF;
        if (n == 0 || n == 0xF)
            return true;

        return false;
    }

}
