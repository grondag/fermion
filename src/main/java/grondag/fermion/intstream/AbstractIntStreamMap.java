package grondag.fermion.intstream;

import grondag.fermion.varia.Useful;

public abstract class AbstractIntStreamMap<V extends AbstractStreamCursor<V>> extends AbstractIntStreamCollection<V> {
    protected float maxFill = 0.75f;

    /**
     * Max size before we rehash.
     */
    protected int rehashLimit;

    /**
     * Entries at 100% full.
     */
    protected int capacity;

    /**
     * Mask to obtain entry index from hash.
     */
    protected int hashMask;

    protected AbstractIntStreamMap(Class<V> cursorType) {
        this(cursorType, 16);
    }

    protected AbstractIntStreamMap(Class<V> cursorType, int startingCapacity) {
        super(cursorType);
        this.capacity = Useful.smallestPowerOfTwo(startingCapacity);
        computeLimitsFromCapacity();
    }

    private void computeLimitsFromCapacity() {
        this.hashMask = this.capacity - 1;
        this.rehashLimit = (int) (this.capacity * this.maxFill);
    }

    /**
     * Call before adding an element to set. Will call
     * {@link #onExpand(int, int, int, int)} if rehash is needed. Also increments
     * size.
     * 
     */
    private void beforeAdd() {
        if (this.size == this.rehashLimit) {
            final int oldCapacity = this.capacity;
            final int oldHashMask = this.hashMask;
            this.capacity *= 2;
            computeLimitsFromCapacity();
            onExpand(oldCapacity, oldHashMask);
        }
    }

    /**
     * Called when a rehash is needed due to expansion.<br>
     * Capacity and hash mask are changed BEFORE this is called.
     */
    private void onExpand(int oldCapacity, int oldHashMash) {
        final IIntStream oldStream = stream;
        final IIntStream newStream = IntStreams.claim();
        int readerIndex = readCursor.index();

        for (int i = 0; i < oldCapacity; i++) {
            // load reader from old stream
            stream = oldStream;
            readCursor.moveToIndexAndRead(i);
            boolean newReadIndex = readCursor.index() == readerIndex;

            // position internal on new stream, find first empty slot
            stream = newStream;
            int putIndex = readCursor.keyHash() & hashMask;
            internal.moveToIndexAndRead(putIndex);
            while (internal.hasValue()) {
                if (++putIndex == this.capacity)
                    putIndex = 0;
                internal.moveToIndexAndRead(putIndex);
            }

            // save reader at new position
            readCursor.moveToIndexAndWrite(putIndex);

            // capture new position of reader in rehashed entries
            if (newReadIndex)
                readerIndex = readCursor.index();

        }

        readCursor.moveToIndexAndRead(readerIndex);
    }

    private void position(V cursor, V toMatch) {
        int index = toMatch.keyHash() & hashMask;
        cursor.moveToIndexAndRead(index);
        while (cursor.hasValue()) {
            // Assuming as fast or faster to compare the key directly without checking hash
            // first
            // because underlying comparison(s) will be int / int either way
            if (cursor.doesKeyMatch(toMatch))
                return;

            if (++index == this.capacity)
                index = 0;
            cursor.moveToIndexAndRead(index);
        }
    }

    /**
     * Adds key/value currently in writer to the map. Returns true if key was not
     * already present.
     * <p>
     * 
     * Does not modify write cursor.
     */
    public boolean put() {
        beforeAdd();
        position(internal, writeCursor);
        final boolean found = internal.hasValue();
        writeCursor.moveToAddressAndWrite(internal.address());
        writeCursor.moveToAddress(0);
        if (!found)
            size++;
        return !found;
    }

    /**
     * Returns true if collection contains key currently in the write cursor.<br>
     * If true, reader will be positioned at the given key.<br>
     * Writer is unchanged and writer values are ignored.<br>
     */
    public boolean find() {
        position(readCursor, writeCursor);
        return readCursor.hasValue();
    }

    /**
     * Remove key/value pair with key currently in the write cursor.<br>
     * Returns true if entry was found.
     * <p>
     * 
     * Reader will not be moved and will reflect deletion if reader happens to be on
     * the deleted entry.p>
     * 
     * Writer is unchanged and writer values are ignored.
     */
    public boolean remove() {
        position(internal, writeCursor);
        if (internal.hasValue()) {
            internal.delete();
            size--;
            if (internal.address() == readCursor.address())
                readCursor.read();
            return true;
        } else
            return false;
    }
}
