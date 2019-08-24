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

import net.minecraft.util.math.BlockPos;

/**
 * Iterators for multi-block regions. Mainly used for species detection.
 */
public interface BlockRegion {
    /** All positions on the surface of the region. */
    public Iterable<BlockPos> surfacePositions();

    /** All positions adjacent to the surface of the region. */
    public Iterable<BlockPos> adjacentPositions();
    
    public static BlockRegion of(BlockPos pos) {
        return new SingleBlockRegion(pos);
    }
}
