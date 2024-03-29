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

import grondag.fermion.position.PackedBlockPos;
import grondag.fermion.position.PackedChunkPos;
import grondag.fermion.varia.Useful;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

/**
 * Maintains a queue of block positions for each world chunk. Per-chunk data is
 * sparse.
 */
public class PerChunkBlockPosQueue {
	private final Long2ObjectOpenHashMap<LongArrayFIFOQueue> chunks = new Long2ObjectOpenHashMap<LongArrayFIFOQueue>();

	public void enqueue(BlockPos pos) {
		final long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);

		LongArrayFIFOQueue chunkQueue = chunks.get(packedChunkPos);

		if (chunkQueue == null) {
			chunkQueue = new LongArrayFIFOQueue();
			chunks.put(packedChunkPos, chunkQueue);
		}
		chunkQueue.enqueue(PackedBlockPos.pack(pos));
	}

	/**
	 * Returns next queued block pos in the same chunk as the given block pos.
	 * Returns null of no positions are queued for that chunk.
	 */
	public BlockPos dequeue(BlockPos pos) {
		final long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);

		final LongArrayFIFOQueue chunkQueue = chunks.get(packedChunkPos);

		if (chunkQueue == null)
			return null;

		if (chunkQueue.isEmpty()) {
			chunks.remove(packedChunkPos);
			return null;
		}

		final BlockPos result = PackedBlockPos.unpack(chunkQueue.dequeueLong());

		if (chunkQueue.isEmpty()) {
			chunks.remove(packedChunkPos);
		}

		return result;
	}

	public void clear() {
		chunks.forEach((k, v) -> v.clear());
		chunks.clear();
	}

	public int sizeInChunkAt(BlockPos pos) {
		final long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);

		final LongArrayFIFOQueue chunkQueue = chunks.get(packedChunkPos);

		if (chunkQueue == null)
			return 0;

		return chunkQueue.size();

	}

	public int sizeInChunksNear(BlockPos pos, int chunkRadius) {
		int result = 0;
		final int radius = Math.min(chunkRadius, Useful.DISTANCE_SORTED_CIRCULAR_OFFSETS_MAX_RADIUS);

		int i = 0;
		Vec3i offset = Useful.getDistanceSortedCircularOffset(i);

		while (offset.getY() <= radius) {
			result += sizeInChunkAt(pos.offset(offset.getX() * 16, 0, offset.getZ() * 16));
			offset = Useful.getDistanceSortedCircularOffset(++i);

		}

		return result;
	}
}
