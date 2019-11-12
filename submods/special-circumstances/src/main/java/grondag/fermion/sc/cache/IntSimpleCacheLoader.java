package grondag.fermion.sc.cache;

public interface IntSimpleCacheLoader<V>
{
	V load(int key);

	// for testing only
	default IntSimpleCacheLoader<V> createNew()
	{
		throw new UnsupportedOperationException();
	}
}
