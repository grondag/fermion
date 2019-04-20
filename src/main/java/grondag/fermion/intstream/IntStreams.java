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

import grondag.fermion.Fermion;
import net.minecraft.util.math.MathHelper;

public abstract class IntStreams {
    public static final int BLOCK_SIZE = 1024;
    static final int BLOCK_MASK = BLOCK_SIZE - 1;
    static final int BLOCK_SHIFT = Integer.bitCount(BLOCK_MASK);

    private static final ArrayBlockingQueue<SimpleStream> simpleStreams = new ArrayBlockingQueue<>(256);

    private static final ArrayBlockingQueue<int[]> bigBlocks = new ArrayBlockingQueue<>(256);

    private static final int[] EMPTY = new int[BLOCK_SIZE];

    private static int[] claimBlock() {
        int[] result = bigBlocks.poll();
        if (result == null)
            return new int[BLOCK_SIZE];
        else {
            System.arraycopy(EMPTY, 0, result, 0, BLOCK_SIZE);
            return result;
        }
    }

    private static void releaseBlock(int[] block) {
        // TODO: remove message
        if (!bigBlocks.offer(block))
            Fermion.LOG.info("Big block buffer was full on block release");
    }

    public static IIntStream claim(int sizeHint) {
        SimpleStream result = simpleStreams.poll();
        if (result == null)
            result = new SimpleStream();
        result.prepare(sizeHint);
        return result;
    }

    public static IIntStream claim() {
        return claim(BLOCK_SIZE);
    }

    private static void release(SimpleStream freeStream) {
        simpleStreams.offer(freeStream);
    }

    /**
     * Uses large blocks only - may be space-inefficient.
     */
    private static class SimpleStream implements IIntStream {
        int[][] blocks = new int[16][];

        int blockCount = 0;
        int capacity = 0;
        boolean isCompact = false;

        private void checkAddress(int address) {
            if (address >= capacity) {
                if (isCompact) {
                    // uncompact
                    int[] lastBlock = blocks[blockCount - 1];
                    int[] newBlock = claimBlock();

                    System.arraycopy(lastBlock, 0, newBlock, 0, lastBlock.length);
                    blocks[blockCount - 1] = newBlock;

                    capacity = BLOCK_SIZE * blockCount;
                    isCompact = false;

                    // if big enough after uncompacting, then we are done
                    if (address < capacity)
                        return;
                }

                int currentBlocks = capacity >> BLOCK_SHIFT;
                int blocksNeeded = (address >> BLOCK_SHIFT) + 1;

                if (blocksNeeded > blocks.length) {
                    int newMax = MathHelper.smallestEncompassingPowerOfTwo(blocksNeeded);
                    int[][] newBlocks = new int[newMax][];
                    System.arraycopy(blocks, 0, newBlocks, 0, blocks.length);
                    blocks = newBlocks;
                }

                for (int i = currentBlocks; i < blocksNeeded; i++)
                    blocks[i] = claimBlock();

                capacity = blocksNeeded << BLOCK_SHIFT;
                blockCount = blocksNeeded;
            }
        }

        @Override
        public int get(int address) {
            return address < capacity ? blocks[address >> BLOCK_SHIFT][address & BLOCK_MASK] : 0;
        }

        public void prepare(int sizeHint) {
            checkAddress(sizeHint - 1);
        }

        private void releaseBlocks() {
            if (blockCount > 0) {
                // don't reuse last block if it isn't a block size
                final int skipIndex = isCompact ? -1 : blockCount - 1;

                for (int i = 0; i < blockCount; i++) {
                    if (i != skipIndex)
                        releaseBlock(blocks[i]);
                    blocks[i] = null;
                }
            }
            blockCount = 0;
            capacity = 0;
            isCompact = false;
        }

        @Override
        public void set(int address, int value) {
            checkAddress(address);
            blocks[address >> BLOCK_SHIFT][address & BLOCK_MASK] = value;
        }

        @Override
        public void clear() {
            // drop last block if we are compacted
            if (isCompact) {
                blockCount--;
                capacity = blockCount * BLOCK_SIZE;
                blocks[blockCount] = null;
                isCompact = false;
            }

            if (blockCount > 0)
                for (int i = 0; i < blockCount; i++)
                    System.arraycopy(EMPTY, 0, blocks[i], 0, BLOCK_SIZE);

        }

        @Override
        public void release() {
            releaseBlocks();
            IntStreams.release(this);
        }

        @Override
        public void copyFrom(int targetAddress, IIntStream source, int sourceAddress, int length) {
            // PERF: special case handling using ArrayCopy for faster transfer
            IIntStream.super.copyFrom(targetAddress, source, sourceAddress, length);
        }

        @Override
        public void compact() {
            if (isCompact || blockCount == 0)
                return;

            int targetBlock = blockCount - 1;

            while (targetBlock >= 0) {
                int[] block = blocks[targetBlock];
                int i = BLOCK_SIZE - 1;
                while (i >= 0 && block[i] == 0)
                    i--;

                if (i == -1) {
                    // release empty blocks
                    releaseBlock(block);
                    blocks[targetBlock] = null;
                    blockCount--;
                    capacity -= BLOCK_SIZE;
                } else if (i == BLOCK_SIZE - 1) {
                    // ending on a block boundary so no need to compact
                    return;
                } else {
                    // partially full block
                    final int shortSize = i + 1;
                    int[] shortBlock = new int[shortSize];
                    System.arraycopy(block, 0, shortBlock, 0, shortSize);
                    releaseBlock(block);
                    blocks[targetBlock] = shortBlock;
                    capacity = (blockCount - 1) * BLOCK_SIZE + shortSize;
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
