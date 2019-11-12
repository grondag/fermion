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

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.World;

/**
 * Keeps lazily-loaded objects, one-per dimension. Not thread-safe.
 */
public abstract class WorldMap<T> extends Int2ObjectOpenHashMap<T> {
	/**
	 *
	 */
	private static final long serialVersionUID = 318003886323074885L;

	@Override
	public synchronized T get(int dimension) {
		T result = super.get(dimension);
		if (result == null) {
			result = load(dimension);
			super.put(dimension, result);
		}
		return result;
	}

	public T get(World world) {
		return this.get(world.dimension.getType().getRawId());
	}

	protected abstract T load(int dimension);
}
