package grondag.fermion.world;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;

import grondag.fermion.varia.Useful;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Maintains a set of block positions for each world chunk. Per-chunk data is
 * sparse.
 */
public abstract class ChunkMap<T> implements Iterable<T> {
    private final Long2ObjectOpenHashMap<T> chunks = new Long2ObjectOpenHashMap<T>();
//    private final BiFunction<? extends ChunkMap<T>, BlockPos, T> entryFactory;

    protected abstract T newEntry(BlockPos pos);

    public boolean contains(BlockPos pos) {
        long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);
        return chunks.containsKey(packedChunkPos);
    }

    public T getOrCreate(BlockPos pos) {
        long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);

        T result = chunks.get(packedChunkPos);

        if (result == null) {
            result = this.newEntry(pos);
            chunks.put(packedChunkPos, result);
        }
        return result;
    }

    public T getIfExists(BlockPos pos) {
        long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);

        return chunks.get(packedChunkPos);
    }

    public void remove(BlockPos pos) {
        long packedChunkPos = PackedChunkPos.getPackedChunkPos(pos);

        chunks.remove(packedChunkPos);
    }

    public void clear() {
        this.chunks.clear();
    }

    public Iterator<T> existingChunksNear(BlockPos pos, int chunkRadius) {
        final int radius = Math.min(chunkRadius, Useful.DISTANCE_SORTED_CIRCULAR_OFFSETS_MAX_RADIUS);

        return new AbstractIterator<T>() {
            private int i = 0;

            @Override
            protected T computeNext() {
                Vec3i offset = Useful.getDistanceSortedCircularOffset(i++);

                while (offset.getY() <= radius) {
                    T result = getIfExists(pos.add(offset.getX() * 16, 0, offset.getZ() * 16));
                    if (result != null)
                        return result;
                    offset = Useful.getDistanceSortedCircularOffset(i++);
                }

                return (T) this.endOfData();
            }
        };

    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.unmodifiableIterator(this.chunks.values().iterator());
    }
}
