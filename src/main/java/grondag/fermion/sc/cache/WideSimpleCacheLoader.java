package grondag.fermion.sc.cache;

public interface WideSimpleCacheLoader<V>
{
	V load(long key1, long key2);

	// for testing only
	default WideSimpleCacheLoader<V> createNew()
	{
		throw new UnsupportedOperationException();
	}
}
