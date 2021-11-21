/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package grondag.fermion.intstream;

import java.util.concurrent.ArrayBlockingQueue;

// TODO: replace with IntStreamProvider
public abstract class IntStreams {
	public static final int BLOCK_SIZE = 1024;
	static final int BLOCK_MASK = BLOCK_SIZE - 1;
	static final int BLOCK_SHIFT = Integer.bitCount(BLOCK_MASK);

	private static final ArrayBlockingQueue<SimpleStream> simpleStreams = new ArrayBlockingQueue<>(256);

	private static final ArrayBlockingQueue<int[]> bigBlocks = new ArrayBlockingQueue<>(256);

	static final int[] EMPTY = new int[BLOCK_SIZE];

	static int[] claimBlock() {
		final int[] result = bigBlocks.poll();
		if (result == null)
			return new int[BLOCK_SIZE];
		else {
			System.arraycopy(EMPTY, 0, result, 0, BLOCK_SIZE);
			return result;
		}
	}

	static void releaseBlock(int[] block) {
		bigBlocks.offer(block);
	}

	public static IntStream claim(int sizeHint) {
		SimpleStream result = simpleStreams.poll();
		if (result == null) {
			result = new SimpleStream();
		}
		result.prepare(sizeHint);
		return result;
	}

	public static IntStream claim() {
		return claim(BLOCK_SIZE);
	}

	static void release(SimpleStream freeStream) {
		simpleStreams.offer(freeStream);
	}
}
