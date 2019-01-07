package grondag.fermion.structures;

public class Tuple2<T> extends Tuple1<T> {
    protected T v1;

    Tuple2(T v0, T v1) {
        super(v0);
        this.v1 = v1;
    }

    Tuple2() {
        super();
    }

    @Override
    public T get(int index) {
        if (index == 0)
            return v0;
        else {
            assert index == 1;
            return v1;
        }
    }

    @Override
    public void set(int index, T value) {
        if (index == 0)
            v0 = value;
        else {
            assert index == 1;
            v1 = value;
        }
    }

    @Override
    public int size() {
        return 2;
    }
}