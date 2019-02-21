package grondag.fermion.sttatecache;

/**
 * Listens to a world and caches terrain height (if applies) and 
 * model state for blocks that implement the ISuperBlock interface.<p>
 * 
 * Must be registered as a world event listener for the world containing blocks that use it.
 * It will invalidate the model state for any blocks that change, and for neighbors according to
 * the neighbor invalidation logic of the block.
 *
 */
public abstract class AbstractWorldStateCache implements IWorldStateCache //,  IWorldEventListener
{
    
//    /**
//     * Called when world is unloaded.
//     */
//    @Override
//    public abstract void clear();
//    
//    protected abstract void invalidateNibble(int chunkX, int nibbleY, int chunkZ);
//    
//    protected void invalidateCacheRange(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ)
//    {
//        final int minChunkX = (minX - 1) >> 4;
//        final int maxChunkX = (maxX + 1) >> 4;
//        
//        final int minNibbleY = (Math.max(0, minY - 2)) >> 4;
//        final int maxNibbleY = (Math.max(255, maxY + 2)) >> 4;
//        
//        final int minChunkZ = (minZ - 1) >> 4;
//        final int maxChunkZ = (maxZ + 1) >> 4;
//        
//        for(int x = minChunkX; x <= maxChunkX; x++)
//        {
//            for(int y = minNibbleY; y <= maxNibbleY; y++)
//            {
//                for(int z = minChunkZ; z <= maxChunkZ; z++)
//                {
//                    this.invalidateNibble(x, y, z);
//                }
//            }
//        }
//    }
//    
//    @Override
//    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
//    {
//        this.invalidateCacheRange(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
//    }
//    
//    @Override
//    public void markBlockRangeForRenderUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
//    {
//        this.invalidateCacheRange(minX, minY, minZ, maxX, maxY, maxZ);
//    }
//
//    @Override
//    public void notifyLightSet(BlockPos pos)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume,
//            float pitch)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void playRecord(SoundEvent soundIn, BlockPos pos)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed,
//            int... parameters)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
//            int... parameters)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void onEntityAdded(Entity entityIn)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void onEntityRemoved(Entity entityIn)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void broadcastSound(int soundID, BlockPos pos, int data)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
//    {
//        // NOOP
//        
//    }
//
//    @Override
//    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
//    {
//        // NOOP
//    }

}
