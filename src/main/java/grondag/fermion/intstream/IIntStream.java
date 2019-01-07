package grondag.fermion.intstream;

public interface IIntStream {
    int get(int address);

    void set(int address, int value);

    default void setFloat(int address, float value) {
        set(address, Float.floatToRawIntBits(value));
    }

    default float getFloat(int address) {
        return Float.intBitsToFloat(get(address));
    }

    default void copyFrom(int targetAddress, IIntStream source, int sourceAddress, int length) {
        for (int i = 0; i < length; i++)
            set(targetAddress + i, source.get(sourceAddress + i));
    }

    default void release() {
    }

    /**
     * Sets all ints in the stream to zero. Does not deallocate any storage.
     */
    void clear();

    /**
     * Releases unused storage and partial blocks, if underlying implementation uses
     * blocks. May cause unpooled allocation and later garbage collection, so use
     * only when going to keep the stream around a while. Will become uncompacted if
     * data are added.
     */
    void compact();

    /**
     * For testing purposes only, the actual number of ints allocated by the stream.
     */
    int capacity();
}
