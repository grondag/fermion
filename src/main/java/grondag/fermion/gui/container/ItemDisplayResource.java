package grondag.fermion.gui.container;

import net.minecraft.item.ItemStack;

public interface ItemDisplayResource {

	ItemStack sampleItemStack();

	boolean isStackEqual(ItemStack heldStack);
}
