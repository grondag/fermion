package grondag.fermion.structures;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assigns an integer handle to object instances that can later be used to
 * retrieve the same instance. Useful when need to serialize an object (in
 * memory) to a primitive type.
 * <p>
 * 
 * Pointers will always be positive, non-zero values.
 * <p>
 * 
 * Safe for concurrent use.
 */
public class ObjectHandle<T> {
    private final AtomicInteger nextHandle = new AtomicInteger(1);

    private volatile T[] instances;

    private final ConcurrentHashMap<T, Integer> map = new ConcurrentHashMap<>();

    public ObjectHandle(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        final T[] a = (T[]) Array.newInstance(clazz, 64);
        instances = a;
    }

    public int toHandle(T object) {
        return map.computeIfAbsent(object, o -> {
            int index = nextHandle.getAndIncrement();
            synchronized (this) {
                if (instances.length < index)
                    instances = Arrays.copyOf(instances, instances.length * 2);
                instances[index - 1] = o;
            }
            return index;
        });
    }

    public T fromHandle(int handle) {
        return instances[handle - 1];
    }
}
