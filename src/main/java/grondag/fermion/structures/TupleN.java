package grondag.fermion.structures;

import java.lang.reflect.Array;

public class TupleN<T> implements ITuple<T> {
    final T[] values;

    /**
     * RETAINS REFERENCE!
     */
    TupleN(T[] values) {
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    TupleN(Class<T> clazz, int order) {
        values = (T[]) Array.newInstance(clazz, order);
    }

    @Override
    public T get(int index) {
        return values[index];
    }

    @Override
    public void set(int index, T value) {
        values[index] = value;
    }

    @Override
    public int size() {
        return values.length;
    }
}
