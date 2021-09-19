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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public class PackedChunkPos {
	private static final int CHUNK_BOUNDARY = PackedBlockPos.WORLD_BOUNDARY >> 4;

	public static long getPackedChunkPos(BlockPos pos) {
		return PackedChunkPos.getPackedChunkPosFromBlockXZ(pos.getX(), pos.getZ());
	}

	public static long getPackedChunkPos(long packedBlockPos) {
		return PackedChunkPos.getPackedChunkPosFromBlockXZ(PackedBlockPos.getX(packedBlockPos),
			PackedBlockPos.getZ(packedBlockPos));
	}

	public static long getPackedChunkPosFromBlockXZ(int blockX, int blockZ) {
		return ((long) blockX >> 4) + PackedChunkPos.CHUNK_BOUNDARY
			| (((long) blockZ >> 4) + PackedChunkPos.CHUNK_BOUNDARY) << 32;
	}

	public static long getPackedChunkPos(ChunkPos chunkPos) {
		return getPackedChunkPosFromChunkXZ(chunkPos.x, chunkPos.z);
	}

	public static long getPackedChunkPos(ChunkAccess chunk) {
		return getPackedChunkPos(chunk.getPos());
	}

	public static long getPackedChunkPosFromChunkXZ(int chunkX, int chunkZ) {
		return (chunkX + PackedChunkPos.CHUNK_BOUNDARY) | (((long) chunkZ + PackedChunkPos.CHUNK_BOUNDARY) << 32);
	}

	public static ChunkPos unpackChunkPos(long packedChunkPos) {
		return new ChunkPos(PackedChunkPos.getChunkXPos(packedChunkPos), PackedChunkPos.getChunkZPos(packedChunkPos));
	}

	/** analog of Chunk.chunkXPos */
	public static int getChunkXPos(long packedChunkPos) {
		return (int) ((packedChunkPos & 0xFFFFFFFF) - PackedChunkPos.CHUNK_BOUNDARY);
	}

	/** analog of Chunk.chunkZPos */
	public static int getChunkZPos(long packedChunkPos) {
		return (int) (((packedChunkPos >> 32) & 0xFFFFFFFF) - PackedChunkPos.CHUNK_BOUNDARY);
	}

	/** analog of Chunk.getXStart() */
	public static int getChunkXStart(long packedChunkPos) {
		return getChunkXPos(packedChunkPos) << 4;
	}

	/** analog of Chunk.getZStart() */
	public static int getChunkZStart(long packedChunkPos) {
		return getChunkZPos(packedChunkPos) << 4;
	}
}
