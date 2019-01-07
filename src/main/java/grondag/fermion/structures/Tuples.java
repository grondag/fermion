package grondag.fermion.structures;

public class Tuples {
    public static <V> ITuple<V> create(Class<V> clazz, int order) {
        switch (order) {
        case 1:
            return new Tuple1<V>();

        case 2:
            return new Tuple2<V>();

        case 3:
            return new Tuple3<V>();

        case 4:
            return new Tuple4<V>();

        default:
            return new TupleN<V>(clazz, order);
        }
    }
}
