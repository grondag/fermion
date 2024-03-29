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

package grondag.fermion.position;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * Serialization of BlockPos to long values with functionality beyond the
 * vanilla serialization methods in the BlockPos class.
 */
public class PackedBlockPos {
	public static final int WORLD_BOUNDARY = 30000000;
	public static final long NULL_POS = Long.MIN_VALUE;
	public static final int NUM_X_BITS = 1
			+ Mth.log2(Mth.smallestEncompassingPowerOfTwo(WORLD_BOUNDARY));
	public static final int NUM_Z_BITS = NUM_X_BITS;
	public static final int NUM_Y_BITS = 8;
	public static final int NUM_EXTRA_BITS = 3;

	public static final int Y_SHIFT = 0 + NUM_Z_BITS;
	public static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
	public static final int EXTRA_SHIFT = X_SHIFT + NUM_Z_BITS;
	public static final long X_MASK = (1L << NUM_X_BITS) - 1L;
	public static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
	public static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
	public static final long EXTRA_MASK = (1L << NUM_EXTRA_BITS) - 1L;
	public static final long POSITION_MASK = (1L << EXTRA_SHIFT) - 1L;

	/**
	 * must be subtracted when packed values are added - otherwise boundary offsets
	 * get included twice
	 */
	public static final long ADDITION_OFFSET = ((long) WORLD_BOUNDARY << X_SHIFT) | WORLD_BOUNDARY;

	public static final long X_INCREMENT = 1L << X_SHIFT;
	public static final long Y_INCREMENT = 1L << Y_SHIFT;
	public static final long Z_INCREMENT = 1L;

	/**
	 * Very similar to MC vanilla method on BlockPos but only uses 8 bits for Y
	 * orthogonalAxis instead of 12. As with that method, uses 26 bits each for X
	 * and Z, leaving 3 higher-order bits available for other information. Not using
	 * the sign bit because it complicates things somewhat and not currently needed.
	 */
	public static final long pack(BlockPos pos) {
		return pack(pos.getX(), pos.getY(), pos.getZ());
	}

	public static final long pack(BlockPos pos, int extra) {
		return pack(pos.getX(), pos.getY(), pos.getZ(), extra);
	}

	/**
	 * Same as version that uses the BlockPos input but with primitive types as
	 * inputs.
	 */
	public static final long pack(int x, int y, int z) {
		if (y < 0) {
			y = 0;
		}

		return (x + WORLD_BOUNDARY & X_MASK) << X_SHIFT | (y & Y_MASK) << Y_SHIFT
				| (z + WORLD_BOUNDARY & Z_MASK);
	}

	public static final long pack(double x, double y, double z) {
		return pack((int) x, (int) y, (int) z);
	}

	/**
	 * Same as version that uses the BlockPos input but with primitive types as
	 * inputs. This version includes 4 extra bits of data that can be used in any
	 * way needed.
	 */
	public static final long pack(int x, int y, int z, int extra) {
		return pack(x, y, z) | ((extra & EXTRA_MASK) << EXTRA_SHIFT);
	}

	public static final BlockPos unpack(long packedValue) {
		final int i = (int) ((packedValue >> X_SHIFT) & X_MASK) - WORLD_BOUNDARY;
		final int j = (int) ((packedValue >> Y_SHIFT) & Y_MASK);
		final int k = (int) (packedValue & Z_MASK) - WORLD_BOUNDARY;
		return new BlockPos(i, j, k);
	}

	/** adds two packed block positions together */
	public static final long add(long first, long second) {
		return first + second - ADDITION_OFFSET;
	}

	public static final long add(long packedPos, int x, int y, int z) {
		return add(packedPos, pack(x, y, z));
	}

	public static final long up(long packedValue) {
		return packedValue + Y_INCREMENT;
	}

	public static final long up(long packedValue, int howFar) {
		return packedValue + Y_INCREMENT * howFar;
	}

	public static final long down(long packedValue) {
		return packedValue - Y_INCREMENT;
	}

	public static final long down(long packedValue, int howFar) {
		return packedValue - Y_INCREMENT * howFar;
	}

	public static final long east(long packedValue) {
		return packedValue + X_INCREMENT;
	}

	public static final long west(long packedValue) {
		return packedValue - X_INCREMENT;
	}

	public static final long north(long packedValue) {
		return packedValue - Z_INCREMENT;
	}

	public static final long south(long packedValue) {
		return packedValue + Z_INCREMENT;
	}

	public static final long northEast(long packedValue) {
		return packedValue - Z_INCREMENT + X_INCREMENT;
	}

	public static final long northWest(long packedValue) {
		return packedValue - Z_INCREMENT - X_INCREMENT;
	}

	public static final long southEast(long packedValue) {
		return packedValue + Z_INCREMENT + X_INCREMENT;
	}

	public static final long southWest(long packedValue) {
		return packedValue + Z_INCREMENT - X_INCREMENT;
	}

	public static final int getX(long packedValue) {
		return (int) ((packedValue >> X_SHIFT) & X_MASK) - WORLD_BOUNDARY;
	}

	public static final int getY(long packedValue) {
		return (int) ((packedValue >> Y_SHIFT) & Y_MASK);
	}

	public static final int getZ(long packedValue) {
		return (int) (packedValue & Z_MASK) - WORLD_BOUNDARY;
	}

	public static final int getExtra(long packedValue) {
		return (int) ((packedValue >> EXTRA_SHIFT) & EXTRA_MASK);
	}

	public static final long setExtra(long packedValue, int extra) {
		return (packedValue & POSITION_MASK) | ((long) extra << EXTRA_SHIFT);
	}

	/** strips the extra bits if there are any */
	public static final long getPosition(long packedValue) {
		return (packedValue & POSITION_MASK);
	}

	public static final long offset(long packedBlockPos, Direction face) {
		switch (face) {
		case DOWN:
			return down(packedBlockPos);
		case EAST:
			return east(packedBlockPos);
		case NORTH:
			return north(packedBlockPos);
		case SOUTH:
			return south(packedBlockPos);
		case UP:
			return up(packedBlockPos);
		case WEST:
			return west(packedBlockPos);
		}

		assert false : "PackedBlockPos offset strangeness.";

		return 0;
	}

	public static final BlockPos.MutableBlockPos unpackTo(long packedBlockPos, BlockPos.MutableBlockPos targetPos) {
		return targetPos.set(getX(packedBlockPos), getY(packedBlockPos), getZ(packedBlockPos));
	}
}
