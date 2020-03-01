package grondag.fermion.tests;

import static org.junit.Assert.assertTrue;

import java.nio.IntBuffer;
import java.util.Random;

import org.junit.jupiter.api.Test;

import grondag.fermion.intstream.IntStreamProvider;
import grondag.fermion.intstream.IntStreamProvider.IntStreamImpl;

class IntStreamProviderTest {

	@Test
	void test() {
		final IntStreamProvider provider = new IntStreamProvider(0x10000, 16, 16);

		final int testSize = 519247;
		final IntStreamImpl a = provider.claim();
		final IntStreamImpl b = provider.claim();
		final int[] testArray = new int[testSize];
		final IntBuffer testBuffer = IntBuffer.allocate(testSize);

		final Random r = new Random(42);


		for (int i = 0; i < testSize; i++) {
			a.set(i, r.nextInt());
			testArray[i] = r.nextInt();
		}

		b.copyFromDirect(0, a, 0, testSize);

		for (int i = 0; i < testSize; i++) {
			assertTrue(a.get(i) == b.get(i));
		}

		// small copies
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize);
			final int y = r.nextInt(testSize);
			final int l = r.nextInt(16);

			a.copyFromDirect(x, b, y, l);

			for (int j = 0; j < l; j++) {
				assertTrue(a.get(x + j) == b.get(y + j));
			}
		}

		// multiblock copies
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize - 0x50000);
			final int y = r.nextInt(testSize - 0x50000);
			final int l = 0x10000 + r.nextInt(0x40000);

			a.copyFromDirect(x, b, y, l);

			for (int j = 0; j < l; j++) {
				assertTrue(a.get(x + j) == b.get(y + j));
			}
		}

		// small copies from array
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize);
			final int y = r.nextInt(testSize);
			final int l = r.nextInt(16);

			a.copyFrom(x, testArray, y, l);

			for (int j = 0; j < l; j++) {
				assertTrue(a.get(x + j) == testArray[y + j]);
			}
		}

		// multiblock copies from array
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize - 0x50000);
			final int y = r.nextInt(testSize - 0x50000);
			final int l = 0x10000 + r.nextInt(0x40000);

			a.copyFrom(x, testArray, y, l);

			for (int j = 0; j < l; j++) {
				assertTrue(a.get(x + j) == testArray[y + j]);
			}
		}

		// small copies to array
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize);
			final int y = r.nextInt(testSize);
			final int l = r.nextInt(16);

			b.copyTo(x, testArray, y, l);

			for (int j = 0; j < l; j++) {
				assertTrue(b.get(x + j) == testArray[y + j]);
			}
		}

		// multiblock copies to array
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize - 0x50000);
			final int y = r.nextInt(testSize - 0x50000);
			final int l = 0x10000 + r.nextInt(0x40000);

			b.copyTo(x, testArray, y, l);

			for (int j = 0; j < l; j++) {
				assertTrue(b.get(x + j) == testArray[y + j]);
			}
		}

		// small copies to IntBuffer
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize);
			final int l = r.nextInt(16);

			testBuffer.clear();
			b.copyTo(x, testBuffer, l);

			for (int j = 0; j < l; j++) {
				assertTrue(b.get(x + j) == testBuffer.get(j));
			}
		}

		// multiblock copies to IntBuffer
		for (int i = 0; i < 10000; i++) {
			final int x = r.nextInt(testSize - 0x50000);
			final int l = 0x10000 + r.nextInt(0x40000);

			testBuffer.clear();
			b.copyTo(x, testBuffer, l);

			for (int j = 0; j < l; j++) {
				assertTrue(b.get(x + j) == testBuffer.get(j));
			}
		}
	}
}
