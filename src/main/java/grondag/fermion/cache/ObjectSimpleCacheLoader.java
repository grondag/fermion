package grondag.fermion.cache;


@FunctionalInterface
public interface ObjectSimpleCacheLoader<K, V> {
    V load(K key);
    
    /** for benchmark testing */
    default ObjectSimpleCacheLoader<K, V> createNew()
    { 
        throw new UnsupportedOperationException();
    }
}
