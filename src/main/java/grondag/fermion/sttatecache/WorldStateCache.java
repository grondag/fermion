package grondag.fermion.sttatecache;

import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import grondag.exotic_matter.block.ISuperBlock;
import grondag.exotic_matter.model.state.ISuperModelState;
import grondag.exotic_matter.terrain.TerrainState;
import grondag.exotic_matter.world.PackedBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldStateCache extends AbstractWorldStateCache
{
    @Nullable protected World world;
    
    @Override
    public void setWorld(@Nullable World world)
    {
        if(this.world != null) this.world.removeEventListener(this);
        this.world = world;
        if(world != null) world.addEventListener(this);
        this.clear();
    }
    
    private final ConcurrentHashMap<Long, WorldStateNibble> nibbles = new ConcurrentHashMap<>();

    private WorldStateNibble getNibble(long packedBlockPos)
    {
        Long key = computeKey(PackedBlockPos.getX(packedBlockPos) >> 4, PackedBlockPos.getY(packedBlockPos) >> 4, PackedBlockPos.getZ(packedBlockPos) >> 4);
        return this.nibbles.computeIfAbsent(key, k -> new WorldStateNibble());
    }
    
    private WorldStateNibble getNibble(BlockPos pos)
    {
        Long key = computeKey(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        return this.nibbles.computeIfAbsent(key, k -> new WorldStateNibble());
    }
    
    @Override
    public ISuperModelState getModelState(ISuperBlock block, IBlockState blockState, BlockPos pos, boolean refreshFromWorld)
    {
        return this.getNibble(pos).getModelState(block, this, blockState, pos, refreshFromWorld);
    }

    @Override
    public int getFlowHeight(long packedBlockPos)
    {
        return this.getNibble(packedBlockPos).getFlowHeight(this, packedBlockPos);
    }
    
    @Override
    public int getFlowHeight(BlockPos pos)
    {
        return this.getNibble(pos).getFlowHeight(this, PackedBlockPos.pack(pos));
    }
    
    @Override
    public void clear()
    {
        this.nibbles.clear();
    }

    //PERF: maybe don't remove the whole thing?
    @Override
    protected void invalidateNibble(int chunkX, int nibbleY, int chunkZ)
    {
        this.nibbles.remove(computeKey(chunkX, nibbleY, chunkZ));
    }

    private long computeKey(int chunkX, int nibbleY, int chunkZ)
    {
        return PackedBlockPos.pack(chunkX, nibbleY, chunkZ);
    }

    @SuppressWarnings("null")
    @Override
    public IBlockAccess wrapped()
    {
        return world;
    }

    @Override
    public TerrainState terrainState(IBlockState state, long packedBlockPos)
    {
        return this.getNibble(packedBlockPos).getTerrainState(this, PackedBlockPos.unpack(packedBlockPos));
    }

    @Override
    public TerrainState terrainState(IBlockState state, BlockPos pos)
    {
        return this.getNibble(pos).getTerrainState(this, pos);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos)
    {
        IBlockAccess world = this.world;
        return world == null ? Blocks.AIR.getDefaultState() : world.getBlockState(pos);
    }
    
    @Override
    public IBlockState getBlockState(long packedBlockPos)
    {
        return getBlockState(PackedBlockPos.unpack(packedBlockPos));
    }
}
