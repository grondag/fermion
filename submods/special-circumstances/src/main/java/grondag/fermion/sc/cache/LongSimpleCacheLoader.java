package grondag.fermion.sc.cache;

public interface LongSimpleCacheLoader<V>
{
    abstract public V load(long key);
    
    // for testing only
    public default LongSimpleCacheLoader<V> createNew()
    { 
        throw new UnsupportedOperationException();
    }
}
