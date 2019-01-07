package grondag.fermion.structures;

/**
 * Base class for tuple objects with an object key. Key is used for equality
 * tests.
 */
public abstract class KeyedTuple<T> {
    public final T key;

    public KeyedTuple(T key) {
        this.key = key;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
        if (other instanceof KeyedTuple) {
            return this.key.equals(((KeyedTuple) other).key);
        } else {
            return false;
        }
    }
}
