package grondag.fermion.statecache;


import net.minecraft.world.World;

/**
 * Non-static model states depend on world state and in some cases deriving the world-dependent
 * components can be computationally expensive, typically because of many block states lookups. <p>
 * 
 * Superblocks that have a tile entity can minimize this cost by caching the model state in the tile entity.
 * But we do not want to add tile entities for simple blocks that don't need to persist state because
 * tile entities come with their own problems and overhead.<p>
 * 
 * Terrain blocks are a special case that have the same problem of model state overhead compounded
 * by the needed to compute terrain height for each neighboring column, which in turn requires
 * the lookup of many block states for each neighbor.<p>
 * 
 */
public interface IWorldStateCache //extends ISuperBlockAccess
{
    default void markBlockRangeForRenderUpdate(int xStart, int i, int zStart, int xEnd, int j, int zEnd) {}

    default void setWorld(World world) {}
    
    void clear();
}
