package grondag.fermion.varia;

import grondag.fermion.serialization.NBTDictionary;
import grondag.fermion.world.PackedBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

/**
 * Data carrier for fixed region definition to reduce number of methods and
 * method calls for fixed regions. Fixed regions can be arbitrarily large and
 * don't have to be cubic - shape depends on interpretation by the placement
 * builder.
 */
public class FixedRegionBounds {
    private static final String TAG_START = NBTDictionary.claim("fixedRegionStart");
    private static final String TAG_END = NBTDictionary.claim("fixedRegionEnd");

    public final BlockPos fromPos;
    public final boolean fromIsCentered;
    public final BlockPos toPos;
    public final boolean toIsCentered;

    public FixedRegionBounds(BlockPos fromPos, boolean fromIsCentered, BlockPos toPos, boolean toIsCentered) {
        this.fromPos = fromPos;
        this.fromIsCentered = fromIsCentered;
        this.toPos = toPos;
        this.toIsCentered = toIsCentered;
    }

    public FixedRegionBounds(CompoundTag tag) {
        final long from = tag.getLong(TAG_START);
        this.fromPos = PackedBlockPos.unpack(from);
        this.fromIsCentered = PackedBlockPos.getExtra(from) == 1;
        final long to = tag.getLong(TAG_END);
        this.toPos = PackedBlockPos.unpack(to);
        this.toIsCentered = PackedBlockPos.getExtra(to) == 1;
    }

    public void saveToNBT(CompoundTag tag) {
        tag.putLong(TAG_START, PackedBlockPos.pack(this.fromPos, this.fromIsCentered ? 1 : 0));
        tag.putLong(TAG_END, PackedBlockPos.pack(this.toPos, this.toIsCentered ? 1 : 0));
    }

    public static boolean isPresentInTag(CompoundTag tag) {
        return tag.containsKey(TAG_START) && tag.containsKey(TAG_END);
    }
}