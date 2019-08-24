package grondag.fermion.sc.cache;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class KeyInterningCache<K> implements ISimpleLoadingCache {
    private final int capacity;
    private final int maxFill;
    private final int positionMask;

    private final Function<K, K> keyInterner;

    private final AtomicInteger backupMissCount = new AtomicInteger(0);

    private volatile KeyCacheState activeState;
    private final AtomicReference<KeyCacheState> backupState = new AtomicReference<>();

    private final Object writeLock = new Object();

    public KeyInterningCache(Function<K, K> keyInterner, int maxSize) {
        this.capacity = 1 << (Long.SIZE - Long.numberOfLeadingZeros((long) (maxSize / LOAD_FACTOR)));
        this.maxFill = (int) (capacity * LOAD_FACTOR);
        this.positionMask = capacity - 1;
        this.keyInterner = keyInterner;
        this.activeState = new KeyCacheState(this.capacity);
        this.clear();
    }

    @Override
    public int size() { return activeState.size.get(); }
    
    @Override
    public void clear() {
        this.activeState = new KeyCacheState(this.capacity);
    }
    
    @SuppressWarnings("unchecked")
    public K get(K key) {
        final KeyCacheState localState = activeState;
        
        int position = key.hashCode() & positionMask;
        
        do {
            final K current = (K) localState.data[position];

            if(current == null) {
                return load(localState, key, position);
            } else if (current.equals(key)) {
                return current;
            } else {
                position = (position + 1) & positionMask;
            }
        } while (true);
    }

    @SuppressWarnings("unchecked")
    private K loadFromBackup(KeyCacheState backup, final K key) {
        int position = (key.hashCode()) & positionMask;
        do {
            final K current = (K) backup.data[position];
            if(current == null) {
                if((backupMissCount.incrementAndGet() & 0xFF) == 0xFF)  {
                    if(backupMissCount.get() > activeState.size.get()) {
                        backupState.compareAndSet(backup, null);
                    }
                }
                return keyInterner.apply(key);
            } else if(current.equals(key)) {
                return current;
            } else {
                position = (position + 1) & positionMask;
            }
        } while(true);
    }

    @SuppressWarnings("unchecked")
    protected K load(KeyCacheState localState, K key, int position) {
        final KeyCacheState backupState = this.backupState.get();
        final K result = backupState == null ? keyInterner.apply(key) : loadFromBackup(backupState, key);
        
        do
        {
            K currentKey;
            synchronized(writeLock)
            {
                currentKey = (K) localState.data[position];
                if(currentKey == null) {
                    //write value start in case another thread tries to read it based on key before we can write it
                    localState.data[position] = result;
                    break;
                }
            }
            
            // small chance another thread added our value before we got our lock
            if(currentKey.equals(key)) return currentKey;
            
            position = (position + 1) & positionMask;
            
        } while(true);
        
        if(localState.size.incrementAndGet() == this.maxFill) {
            KeyCacheState newState = new KeyCacheState(this.capacity);
            this.backupState.set(this.activeState);
            this.activeState = newState;
            this.backupMissCount.set(0);
        }
        return result;
    }
}