package grondag.fermion.intstream;

import java.nio.IntBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.util.math.MathHelper;

public class IntStreamProvider {
	final int blockSize;
	int blockMask;
	final int blockShift;
	final int[] emptyBlock;

	final ArrayBlockingQueue<IntStreamImpl> streams;
	final ArrayBlockingQueue<int[]> blockPool;

	public IntStreamProvider(int blockSizeIn, int streamPoolSize, int blockPoolSize) {
		blockSize = MathHelper.smallestEncompassingPowerOfTwo(blockSizeIn);
		blockMask = blockSize - 1;
		blockShift = Integer.bitCount(blockMask);
		emptyBlock = new int[blockSize];

		streams = new ArrayBlockingQueue<>(MathHelper.smallestEncompassingPowerOfTwo(streamPoolSize));
		blockPool = new ArrayBlockingQueue<>(MathHelper.smallestEncompassingPowerOfTwo(blockPoolSize));
	}

	int[] claimBlock() {
		final int[] result = blockPool.poll();
		return result == null ? new int[blockSize] : result;
	}

	public IntStreamImpl claim(int sizeHint) {
		IntStreamImpl result = streams.poll();

		if (result == null) {
			result = new IntStreamImpl();
		}

		result.prepare(sizeHint);
		return result;
	}

	public IntStreamImpl claim() {
		return claim(blockSize);
	}

	public class IntStreamImpl implements IntStream {
		int[][] blocks = new int[16][];

		int blockCount = 0;
		int capacity = 0;
		boolean isCompact = false;

		private void checkAddress(int address) {
			if (address >= capacity) {
				if (isCompact) {
					// uncompact
					final int[] lastBlock = blocks[blockCount - 1];
					final int[] newBlock = claimBlock();

					System.arraycopy(lastBlock, 0, newBlock, 0, lastBlock.length);
					blocks[blockCount - 1] = newBlock;

					capacity = blockSize * blockCount;
					isCompact = false;

					// if big enough after uncompacting, then we are done
					if (address < capacity)
						return;
				}

				final int currentBlocks = capacity >> blockShift;
					final int blocksNeeded = (address >> blockShift) + 1;

					if (blocksNeeded > blocks.length) {
						final int newMax = MathHelper.smallestEncompassingPowerOfTwo(blocksNeeded);
						final int[][] newBlocks = new int[newMax][];
						System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
						blocks = newBlocks;
					}

					for (int i = currentBlocks; i < blocksNeeded; i++) {
						blocks[i] = claimBlock();
					}

					capacity = blocksNeeded << blockShift;
					blockCount = blocksNeeded;
			}
		}

		@Override
		public int get(int address) {
			return address < capacity ? blocks[address >> blockShift][address & blockMask] : 0;
		}

		public void prepare(int sizeHint) {
			checkAddress(sizeHint - 1);
		}

		private void releaseBlocks() {
			if (blockCount > 0) {
				// don't reuse last block if it isn't a block size
				final int skipIndex = isCompact ? -1 : blockCount - 1;

				for (int i = 0; i < blockCount; i++) {
					if (i != skipIndex) {
						zeroAndReleaseBlock(i);
					}

					blocks[i] = null;
				}
			}

			blockCount = 0;
			capacity = 0;
			isCompact = false;
		}

		void zeroAndReleaseBlock(int blockIndex) {
			final int[] block = blocks[blockIndex];
			blocks[blockIndex] = null;
			System.arraycopy(emptyBlock, 0, block, 0, blockSize);
			blockPool.offer(block);
		}

		@Override
		public void set(int address, int value) {
			checkAddress(address);
			blocks[address >> blockShift][address & blockMask] = value;
		}

		@Override
		public void clear() {
			// drop last block if we are compacted
			if (isCompact) {
				blockCount--;
				capacity = blockCount * blockSize;
				blocks[blockCount] = null;
				isCompact = false;
			}

			if (blockCount > 0) {
				for (int i = 0; i < blockCount; i++) {
					System.arraycopy(emptyBlock, 0, blocks[i], 0, blockSize);
				}
			}
		}

		/**
		 * Releases all but the last block and does not zero contents
		 * in kept or released blocks. <p>
		 *
		 * Only use when you know every position will be written.
		 */
		public void reset() {
			if (blockCount > 1) {
				for (int i = 1; i < blockCount; i++) {
					blockPool.offer(blocks[i]);
					blocks[i] = null;
				}

				blockCount = 1;
				capacity = blockSize;
			}
		}

		@Override
		public void release() {
			releaseBlocks();
			streams.offer(this);
		}

		protected int blockIndex(int address) {
			return address >> blockShift;
		}

		/** should be faster than default implementation */
		public void copyFromDirect(int targetAddress, IntStreamImpl source, int sourceAddress, int length) {
			final int lastTargetAddress = targetAddress + length - 1;
			final int lastTargetBlock = blockIndex(lastTargetAddress);
			checkAddress(lastTargetAddress);

			int t = targetAddress;
			final int sourceOffset = sourceAddress - targetAddress;

			while (t <= lastTargetAddress) {
				final int myBlockIndex = blockIndex(t);
				final int myBlockAddress = t & blockMask;
				final int runLength = myBlockIndex == lastTargetBlock ? lastTargetAddress - t + 1 : blockSize - myBlockAddress;
				copyFromDirectInner(blocks[myBlockIndex], myBlockAddress, source, t + sourceOffset, runLength);
				t += runLength;
			}
		}

		private void copyFromDirectInner(int[] targetBlock, int targetBlockAddress, IntStreamImpl source, int sourceAddress, int length) {
			final int firstSourceBlock = blockIndex(sourceAddress);
			final int lastSourceAddress = sourceAddress + length - 1;
			final int lastSourceBlock = blockIndex(lastSourceAddress);

			if (firstSourceBlock == lastSourceBlock) {
				System.arraycopy(source.blocks[firstSourceBlock], sourceAddress & blockMask, targetBlock, targetBlockAddress, length);
			} else {
				final int firstBlockAddress = sourceAddress & blockMask;
				final int firstRunLength = blockSize - firstBlockAddress;
				System.arraycopy(source.blocks[firstSourceBlock], firstBlockAddress, targetBlock, targetBlockAddress, firstRunLength);
				System.arraycopy(source.blocks[lastSourceBlock], 0, targetBlock, targetBlockAddress + firstRunLength, length - firstRunLength);
			}
		}

		@Override
		public void copyFrom(int targetAddress, int[] source, int sourceIndex, int length) {
			if (targetAddress + length > capacity || (targetAddress & blockMask) + length >= blockSize) {
				copyFromSlow(targetAddress, source, sourceIndex, length);
			} else {
				copyFromFast(targetAddress, source, sourceIndex, length);
			}
		}

		private void copyFromFast(int targetAddress, int[] source, int sourceIndex, int length) {
			System.arraycopy(source, sourceIndex, blocks[blockIndex(targetAddress)], targetAddress & blockMask, length);
		}

		private void copyFromSlow(int targetAddress, int[] source, int sourceIndex, int length) {
			final int lastTargetAddress = targetAddress + length - 1;
			final int lastTargetBlock = blockIndex(lastTargetAddress);
			checkAddress(lastTargetAddress);

			int t = targetAddress;
			final int sourceOffset = sourceIndex - targetAddress;

			while (t <= lastTargetAddress) {
				final int myBlockIndex = blockIndex(t);
				final int myBlockAddress = t & blockMask;
				final int runLength = myBlockIndex == lastTargetBlock ? lastTargetAddress - t + 1 : blockSize - myBlockAddress;
				System.arraycopy(source, t + sourceOffset, blocks[myBlockIndex], myBlockAddress, runLength);
				t += runLength;
			}
		}

		@Override
		public void copyTo(int sourceAddress, int[] target, int targetIndex, int length) {
			final int lastSourceAddress = sourceAddress + length - 1;
			final int lastSourceBlock = blockIndex(lastSourceAddress);
			checkAddress(lastSourceAddress);

			int n = sourceAddress;
			final int targetOffset = targetIndex - sourceAddress;

			while (n <= lastSourceAddress) {
				final int myBlockIndex = blockIndex(n);
				final int myBlockAddress = n & blockMask;
				final int runLength = myBlockIndex == lastSourceBlock ? lastSourceAddress - n + 1 : blockSize - myBlockAddress;
				System.arraycopy(blocks[myBlockIndex], myBlockAddress, target, n + targetOffset, runLength);
				n += runLength;
			}
		}

		public void copyTo(int sourceAddress, IntBuffer intBuffer, int length) {
			final int lastSourceAddress = sourceAddress + length - 1;
			final int lastSourceBlock = blockIndex(lastSourceAddress);
			checkAddress(lastSourceAddress);

			int n = sourceAddress;

			while (n <= lastSourceAddress) {
				final int myBlockIndex = blockIndex(n);
				final int myBlockAddress = n & blockMask;
				final int runLength = myBlockIndex == lastSourceBlock ? lastSourceAddress - n + 1 : blockSize - myBlockAddress;
				intBuffer.put(blocks[myBlockIndex], myBlockAddress, runLength);
				n += runLength;
			}
		}

		@Override
		public void compact() {
			if (isCompact || blockCount == 0) {
				return;
			}

			int targetBlock = blockCount - 1;

			while (targetBlock >= 0) {
				final int[] block = blocks[targetBlock];
				int i = blockSize - 1;

				while (i >= 0 && block[i] == 0) {
					i--;
				}

				if (i == -1) {
					// release empty blocks
					zeroAndReleaseBlock(targetBlock);
					blocks[targetBlock] = null;
					blockCount--;
					capacity -= blockSize;
				} else if (i == blockSize - 1)
					// ending on a block boundary so no need to compact
					return;
				else {
					// partially full block
					final int shortSize = i + 1;
					final int[] shortBlock = new int[shortSize];
					System.arraycopy(block, 0, shortBlock, 0, shortSize);
					zeroAndReleaseBlock(targetBlock);
					blocks[targetBlock] = shortBlock;
					capacity = (blockCount - 1) * blockSize + shortSize;
					isCompact = true;
					return;
				}

				targetBlock--;
			}
		}

		@Override
		public int capacity() {
			return capacity;
		}
	}
}
