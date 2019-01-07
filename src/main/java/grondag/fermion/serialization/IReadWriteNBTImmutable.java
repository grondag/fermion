package grondag.fermion.serialization;

import net.minecraft.nbt.CompoundTag;

/**
 * NBT read/write interface for classes with immutable values
 */
public interface IReadWriteNBTImmutable<T> {
    /**
     * Should return the instance used to invoke the method if tag is not present or
     * invalid
     */
    T deserializeNBT(CompoundTag tag);

    void serializeNBT(CompoundTag tag);

    default CompoundTag serializeNBT() {
        CompoundTag result = new CompoundTag();
        this.serializeNBT(result);
        return result;
    }
}
