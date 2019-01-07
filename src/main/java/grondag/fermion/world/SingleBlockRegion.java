package grondag.fermion.world;

import net.minecraft.util.math.BlockPos;

public class SingleBlockRegion implements IBlockRegion {

    public final BlockPos pos;

    public SingleBlockRegion(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public Iterable<BlockPos.Mutable> surfacePositions() {
        return BlockPos.method_10068(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public Iterable<BlockPos.Mutable> adjacentPositions() {
        return CubicBlockRegion.getAllOnBoxSurfaceMutable(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1,
                pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

}
