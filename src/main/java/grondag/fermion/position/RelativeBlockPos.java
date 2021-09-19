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

/**
 * Stores XYZ offsets in a 32-bit integer that also acts as a hash. Uses 8 bits
 * for Y and 12 bits each for X & Z. Y values are stored as an absolute
 * coordinate (not an offset) with values from 0 to 255. (This covers all
 * possible values, no offset needed.)
 *
 * X and Z values range from -2047 to +2047.
 *
 *
 * need to find by lowest closest
 */
public class RelativeBlockPos {
	public static int getKey(BlockPos pos, BlockPos origin) {
		final int x = (pos.getX() + 2047 - origin.getX()) & 0xFFF;
		final int z = (pos.getZ() + 2047 - origin.getZ()) & 0xFFF;
		final int y = pos.getY() & 0xFF;
		return x | y << 12 | z << 20;
	}

	public static BlockPos getPos(int key, BlockPos origin) {
		final int x = (key & 0xFFF) - 2047 + origin.getX();
		final int z = ((key >> 20) & 0xFFF) - 2047 + origin.getZ();
		final int y = (key >> 12) & 0xFF;
		return new BlockPos(x, y, z);
	}
}
