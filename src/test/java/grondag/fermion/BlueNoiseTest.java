package grondag.fermion;

import org.junit.Test;

import grondag.fermion.varia.BlueNoise;

public class BlueNoiseTest {

    @Test
    public void test() {
        final int SIZE = 256;
        long start = System.nanoTime();
        BlueNoise noise = BlueNoise.create(SIZE, 16, 37);
        long end = System.nanoTime();

        for (int y = 0; y < SIZE; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < SIZE; x++) {
                sb.append(noise.isSet(x, y) ? " O " : " - ");
            }
            System.out.println(sb.toString());
        }
        System.out.println("Create duration = " + (end - start) / 1000000000.0 + "s");
    }

}