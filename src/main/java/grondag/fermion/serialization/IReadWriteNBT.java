package grondag.fermion.serialization;

import net.minecraft.nbt.CompoundTag;

/**
 * Slightly more flexible version of INBTSerializable that allows for writing to
 * an existing tag instead of always creating a new one.
 */
public interface IReadWriteNBT {
    void deserializeNBT(CompoundTag tag);

    void serializeNBT(CompoundTag tag);

    default CompoundTag serializeNBT() {
        CompoundTag result = new CompoundTag();
        this.serializeNBT(result);
        return result;
    }
}
