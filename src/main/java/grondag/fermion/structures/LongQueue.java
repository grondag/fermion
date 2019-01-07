package grondag.fermion.structures;

import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;

/**
 * Extension of the FastUtil FIFO long queue with immutable access to members
 * for serialization or iteration.
 */
@SuppressWarnings("serial")
public class LongQueue extends LongArrayFIFOQueue {
    public LongQueue() {
        super();
    }

    public LongQueue(final int capacity) {
        super(capacity);
    }

    public final long[] toArray() {
        long[] result = new long[this.size()];

        if (result.length > 0) {
            if (start >= end) {
                System.arraycopy(array, start, result, 0, length - start);
                System.arraycopy(array, 0, result, length - start, end);
            } else
                System.arraycopy(array, start, result, 0, end - start);
        }

        return result;
    }
}
