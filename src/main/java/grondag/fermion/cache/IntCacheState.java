package grondag.fermion.cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class IntCacheState<V>
{

    protected AtomicInteger size = new AtomicInteger(0);
    protected final AtomicReference<V> zeroValue = new AtomicReference<V>();
    protected final int[] keys;
    protected final V[] values;


    
    @SuppressWarnings("unchecked")
    public IntCacheState(int capacityIn)
    {
        this.keys = new int[capacityIn];
        this.values = (V[]) new Object[capacityIn];
    }
}