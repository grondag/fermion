package grondag.fermion.sc.cache;

public interface ISimpleLoadingCache
{
	float LOAD_FACTOR = 0.7F;

	void clear();

	int size();
}
