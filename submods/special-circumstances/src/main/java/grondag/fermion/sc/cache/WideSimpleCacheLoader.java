package grondag.fermion.sc.cache;

public interface WideSimpleCacheLoader<V>
{
    abstract public V load(long key1, long key2);
    
    // for testing only
    public default WideSimpleCacheLoader<V> createNew()
    { 
        throw new UnsupportedOperationException();
    }
}
