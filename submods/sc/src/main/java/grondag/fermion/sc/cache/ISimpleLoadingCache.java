package grondag.fermion.sc.cache;

public interface ISimpleLoadingCache
{
    public static final float LOAD_FACTOR = 0.7F;
    
    public void clear();
    
    public int size();
}
