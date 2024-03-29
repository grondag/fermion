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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WorldChunkBlockMap<T> extends WorldMap<ChunkMap<ChunkBlockMap<T>>> {

	/**
	 *
	 */
	private static final long serialVersionUID = 4048164246377574473L;

	@Override
	protected ChunkMap<ChunkBlockMap<T>> load(Level world) {
		return new ChunkMap<ChunkBlockMap<T>>() {

			@Override
			protected ChunkBlockMap<T> newEntry(BlockPos pos) {
				return new ChunkBlockMap<>(pos);
			}
		};
	}
}
