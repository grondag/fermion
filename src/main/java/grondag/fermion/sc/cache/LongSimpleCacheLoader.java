package grondag.fermion.sc.cache;

public interface LongSimpleCacheLoader<V>
{
	V load(long key);

	// for testing only
	default LongSimpleCacheLoader<V> createNew()
	{
		throw new UnsupportedOperationException();
	}
}
