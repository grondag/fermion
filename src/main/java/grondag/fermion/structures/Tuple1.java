package grondag.fermion.structures;

public class Tuple1<T> implements ITuple<T> {
    protected T v0;

    Tuple1(T v0) {
        this.v0 = v0;
    }

    Tuple1() {
    }

    @Override
    public T get(int index) {
        assert index == 0;
        return v0;
    }

    @Override
    public void set(int index, T value) {
        assert index == 0;
        v0 = value;
    }

    @Override
    public int size() {
        return 1;
    }

}