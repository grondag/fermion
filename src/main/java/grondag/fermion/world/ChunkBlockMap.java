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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

public class ChunkBlockMap<T> {
	protected final HashMap<BlockPos, T> blocks = new HashMap<BlockPos, T>();

	protected List<Pair<BlockPos, T>> sortedList;

	public final AABB chunkAABB;
	//    public final int chunkX;
	//    public final int chunkZ;

	private static final int CHUNK_START_MASK = ~0xF;

	public ChunkBlockMap(BlockPos pos) {
		//        this.chunkX = pos.getX() >> 4;
		//        this.chunkZ = pos.getZ() >> 4;
		this.chunkAABB = new AABB(pos.getX() & CHUNK_START_MASK, 0, pos.getZ() & CHUNK_START_MASK,
			pos.getX() | 0xF, 255, pos.getZ() | 0xF);
	}

	/**
	 * ASSUMES POSITION IS IN THIS CHUNK!
	 */
	public T get(BlockPos pos) {
		return this.get(pos);
	}

	/**
	 * ASSUMES POSITION IS IN THIS CHUNK!
	 */
	public boolean containsValueAt(BlockPos pos) {
		return this.blocks.containsKey(pos);
	}

	/**
	 * ASSUMES POSITION IS IN THIS CHUNK! Returns previous value.
	 */
	public T put(BlockPos pos, T value) {
		this.sortedList = null;
		return this.blocks.put(pos, value);
	}

	/**
	 * ASSUMES POSITION IS IN THIS CHUNK! Returns previous value.
	 */
	public T remove(BlockPos pos) {
		this.sortedList = null;
		return this.blocks.remove(pos);
	}

	public int size() {
		return this.blocks.size();
	}

	public boolean isEmpty() {
		return this.blocks.isEmpty();
	}

	public void clear() {
		this.blocks.clear();
	}

	/**
	 * Sorted from bottom to top.
	 */
	public List<Pair<BlockPos, T>> asSortedList() {
		if (this.sortedList == null) {
			if (this.blocks.isEmpty()) {
				this.sortedList = ImmutableList.of();
			} else {
				this.sortedList = this.blocks.entrySet().stream()
					.map(new Function<HashMap.Entry<BlockPos, T>, Pair<BlockPos, T>>() {

						@Override
						public Pair<BlockPos, T> apply(Entry<BlockPos, T> t) {
							return Pair.of(t.getKey(), t.getValue());
						}
					}).sorted(new Comparator<Pair<BlockPos, T>>() {

						@Override
						public int compare(Pair<BlockPos, T> o1, Pair<BlockPos, T> o2) {
							return Integer.compare(o1.getLeft().getY(), o2.getLeft().getY());
						}
					}).collect(ImmutableList.toImmutableList());
			}
		}
		return this.sortedList;
	}
}
