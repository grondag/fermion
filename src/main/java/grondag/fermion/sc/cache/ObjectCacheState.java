package grondag.fermion.sc.cache;

import java.util.concurrent.atomic.AtomicInteger;


public class ObjectCacheState {
	protected AtomicInteger size = new AtomicInteger(0);

	final Object[] kv;

	ObjectCacheState(int capacityIn)
	{
		kv = new Object[capacityIn * 2];
	}
}