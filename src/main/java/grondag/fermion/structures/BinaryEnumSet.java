package grondag.fermion.structures;

import java.lang.reflect.Array;

/**
 * Used for fast mapping of a enum to boolean values serialized to a numeric
 * primitive.
 * 
 * @author grondag
 *
 */
public class BinaryEnumSet<T extends Enum<?>> {
    private T[] values;

    private final Class<T> clazz;

    public BinaryEnumSet(Class<T> clazz) {
        this.clazz = clazz;
        this.values = clazz.getEnumConstants();
    }

    /**
     * Number of distinct values for flag values produced and consumed by this
     * instance. Derivation is trivially simple. Main use is for clarity.
     */
    public final int combinationCount() {
        return 2 << (values.length - 1);
    }

    public final int getFlagsForIncludedValues(@SuppressWarnings("unchecked") T... included) {
        int result = 0;
        for (T e : included) {
            result |= (1 << e.ordinal());
        }
        return result;
    }

    public int getFlagsForIncludedValues(T v0, T v1, T v2, T v3) {
        return (1 << v0.ordinal()) | (1 << v1.ordinal()) | (1 << v2.ordinal()) | (1 << v3.ordinal());
    }

    public int getFlagsForIncludedValues(T v0, T v1, T v2) {
        return (1 << v0.ordinal()) | (1 << v1.ordinal()) | (1 << v2.ordinal());
    }

    public final int getFlagsForIncludedValues(T v0, T v1) {
        return (1 << v0.ordinal()) | (1 << v1.ordinal());
    }

    public final int getFlagForValue(T v0) {
        return (1 << v0.ordinal());
    }

    public final int setFlagForValue(T v, int flagsIn, boolean isSet) {
        if (isSet) {
            return flagsIn | (1 << v.ordinal());
        } else {
            return flagsIn & ~(1 << v.ordinal());
        }
    }

    public final boolean isFlagSetForValue(T v, int flagsIn) {
        return (flagsIn & (1 << v.ordinal())) != 0;
    }

    public final T[] getValuesForSetFlags(int flagsIn) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(clazz, Integer.bitCount(flagsIn));

        final int bitCount = Integer.SIZE - Integer.numberOfLeadingZeros(flagsIn);
        int j = 0;
        for (int i = 0; i < bitCount; i++) {
            if ((flagsIn & (1 << i)) != 0) {
                result[j++] = values[i];
            }
        }
        return result;
    }
}
