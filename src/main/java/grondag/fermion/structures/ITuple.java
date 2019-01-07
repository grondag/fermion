package grondag.fermion.structures;

public interface ITuple<T> {
    public T get(int index);

    public void set(int index, T value);

    public int size();
}
