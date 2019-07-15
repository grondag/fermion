package grondag.fermion.cache;

import java.util.concurrent.atomic.AtomicInteger;


public class KeyCacheState {
    protected final AtomicInteger size = new AtomicInteger(0);

    final Object[] data;

    KeyCacheState(int capacityIn)
    {
        this.data = new Object[capacityIn];
    }
}