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

import net.minecraft.util.math.BlockPos;

public class SingleBlockRegion implements IBlockRegion {

    public final BlockPos pos;

    public SingleBlockRegion(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Iterable<BlockPos> surfacePositions() {
        return BlockPos.iterate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Iterable<BlockPos> adjacentPositions() {
        return CubicBlockRegion.getAllOnBoxSurfaceMutable(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

}
