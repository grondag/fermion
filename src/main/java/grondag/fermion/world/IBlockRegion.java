package grondag.fermion.world;

import net.minecraft.util.math.BlockPos;

/**
 * Iterators for multi-block regions. Mainly used for species detection.
 */
public interface IBlockRegion {
    /** All positions on the surface of the region. */
    public Iterable<BlockPos.Mutable> surfacePositions();

    /** All positions adjacent to the surface of the region. */
    public Iterable<BlockPos.Mutable> adjacentPositions();
}
