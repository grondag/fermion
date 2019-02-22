package grondag.fermion.statecache;

/**
 * Cache for a single 16x16x16 region.
 */
public class WorldStateNibble
{
//    private ISuperModelState[] modelStates;
//    private byte[] flowHeights;
//    private TerrainState[] terrainStates;
//    
////    private static AtomicInteger terrainLookups = new AtomicInteger(0);
////    private static AtomicInteger terrainHits = new AtomicInteger(0);
////    private static AtomicInteger modelLookups = new AtomicInteger(0);
////    private static AtomicInteger modelHits = new AtomicInteger(0);
//    
//    public ISuperModelState getModelState(ISuperBlock block, ISuperBlockAccess world, IBlockState blockState, BlockPos pos, boolean refreshFromWorld)
//    {
//        ISuperModelState result;
//        final int index =  computeIndex(pos);
//        ISuperModelState[] modelStates = this.modelStates;
//        
//        if(modelStates == null)
//        {
//            modelStates = new ISuperModelState[4096];
//            result = block.getModelStateAssumeStateIsCurrent(blockState, world, pos, refreshFromWorld);
//            
//            // don't save in cache if not being refreshed - don't want to cache stale states
//            if(refreshFromWorld) modelStates[index] = result;
//            
//            this.modelStates = modelStates;
//        }
//        else
//        {
//            result = modelStates[index];
//            if(result == null)
//            {
//                result = block.getModelStateAssumeStateIsCurrent(blockState, world, pos, refreshFromWorld);
//                // don't save in cache if not being refreshed - don't want to cache stale states
//                if(refreshFromWorld) modelStates[index] = result;
//            }
//            else
//            {
//                if(refreshFromWorld) result.refreshFromWorld(blockState, world, pos);
////                modelHits.incrementAndGet();
//            }
//        }
//        
////        if((modelLookups.incrementAndGet() & 0xFFF) == 0xFFF) System.out.println("World state cache model hit rate = " + modelHits.get() / (float) modelLookups.get()); 
//        
//        return result;
//    }
//    
//    public int getFlowHeight(ISuperBlockAccess world, long packedBlockPos)
//    {
//        int result;
//        final int index =  computeIndex(packedBlockPos);
//        byte[] flowHeights = this.flowHeights;
//        
//        if(flowHeights == null)
//        {
//            flowHeights = new byte[4096];
//            Arrays.fill(flowHeights, Byte.MIN_VALUE);
//            result = TerrainState.getFlowHeight(world, packedBlockPos);
//            flowHeights[index] = (byte) result;
//            this.flowHeights = flowHeights;
//        }
//        else
//        {
//            result = flowHeights[index];
//            if(result == Byte.MIN_VALUE)
//            {
//                result = TerrainState.getFlowHeight(world, packedBlockPos);
//                flowHeights[index] = (byte) result;
//            }
////            else terrainHits.incrementAndGet();
//        }
//        
////        if((terrainLookups.incrementAndGet() & 0xFFFF) == 0xFFFF) System.out.println("World state cache terrain flow height hit rate = " + terrainHits.get() / (float) terrainLookups.get()); 
//
//        return result;
//    }
//    
//    public TerrainState getTerrainState(ISuperBlockAccess world, BlockPos pos)
//    {
//        TerrainState result;
//        final int index =  computeIndex(pos);
//        TerrainState[] terrainStates = this.terrainStates;
//        
//        if(terrainStates == null)
//        {
//            terrainStates = new TerrainState[4096];
//            result = TerrainState.terrainState(world, world.getBlockState(pos), pos);
//            terrainStates[index] = result;
//            this.terrainStates = terrainStates;
//        }
//        else
//        {
//            result = terrainStates[index];
//            if(result == null)
//            {
//                result = TerrainState.terrainState(world, world.getBlockState(pos), pos);
//                terrainStates[index] = result;
//            }
//        }
//        return result;
//    }
//
//    private int computeIndex(BlockPos pos)
//    {
//        return (pos.getX() & 0xF) | ((pos.getY() & 0xF) << 4) | ((pos.getZ() & 0xF) << 8);
//    }
//    
//    private int computeIndex(long packedBlockPos)
//    {
//        return (PackedBlockPos.getX(packedBlockPos) & 0xF) | ((PackedBlockPos.getY(packedBlockPos) & 0xF) << 4) | ((PackedBlockPos.getZ(packedBlockPos) & 0xF) << 8);
//    }
}
