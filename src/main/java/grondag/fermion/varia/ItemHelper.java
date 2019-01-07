package grondag.fermion.varia;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ItemHelper {
    // for unit testing
    public static class TestItemStack {

        public Item item;
        public int size;
        public CompoundTag tag;
        public int meta;

        public TestItemStack(Item item, int size, int meta, CompoundTag tag) {
            this.item = item;
            this.size = size;
            this.meta = meta;
            this.tag = tag;
        }

        public Item getItem() {
            return this.item;
        }

        public boolean hasTagCompound() {
            return this.tag != null;
        }

        public CompoundTag getTagCompound() {
            return this.tag;
        }

        public int getMetadata() {
            return this.meta;
        }

        public boolean areCapsCompatible(TestItemStack stack1) {
            return true;
        }

    }

    public static CompoundTag getOrCreateStackTag(ItemStack stack) {
        CompoundTag result = stack.getTag();
        if (result == null) {
            result = new CompoundTag();
            stack.setTag(result);
        }
        return result;
    }

    /**
     * True if item stacks can stack with each other - does not check for stack
     * limit
     */
    public static boolean canStacksCombine(ItemStack stack1, ItemStack stack2)
    // public static boolean canStacksCombine(ItemStack stack1, ItemStack stack2)
    {
        if (stack1.isEmpty()) {
            return false;
        } else if (stack1.getItem() != stack2.getItem()) {
            return false;
        } else if (stack1.hasTag() ^ stack2.hasTag()) {
            return false;
        } else if (stack1.hasTag() && !stack1.getTag().equals(stack2.getTag())) {
            return false;
        }

        else if (stack1.getAmount() != stack2.getAmount()) {
            return false;
        }

        return true;
    }

    /**
     * Returns hash codes that should be equal if
     * {@link #canStacksCombine(ItemStack, ItemStack)} returns true; Does not
     * consider capabilities in hash code.
     */
//    public static int stackHashCode(ItemStack stack)
    public static int stackHashCode(TestItemStack stack) {
        Item item = stack.getItem();

        if (item == null)
            return 0;

        int hash = item.hashCode();

        if (stack.hasTagCompound()) {
            hash = hash * 7919 + stack.getTagCompound().hashCode();
        }

        if (stack.getMetadata() != 0) {
            hash = hash * 7919 + stack.getMetadata();
        }

        return hash;
    }
}
