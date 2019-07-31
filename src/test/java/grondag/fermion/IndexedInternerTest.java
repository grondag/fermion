package grondag.fermion;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import grondag.fermion.structures.IndexedInterner;

class IndexedInternerTest {

    @Test
    void test() {
        final IndexedInterner<Integer> handler = new IndexedInterner<Integer>(Integer.class);

        final Integer[] data = new Integer[320000];

        final int[] handles = new int[data.length];

        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }

        Arrays.stream(data).parallel().forEach(d -> handles[d] = handler.toHandle(d));

        for (int i = 0; i < data.length; i++) {
            Integer o = handler.fromHandle(handles[i]);
            assert o == data[i];
        }

        Arrays.stream(data).parallel().forEach(d -> {
            assert handler.fromHandle(handles[d]) == data[d];
        });

    }

}
