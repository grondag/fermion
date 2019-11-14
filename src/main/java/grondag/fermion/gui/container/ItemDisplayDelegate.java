package grondag.fermion.gui.container;

import net.minecraft.item.ItemStack;

/**
 * Client-side representation of server inventory that supports
 * very large quantities and slotless/virtual containers via handles.
 */
public interface ItemDisplayDelegate {
	/**
	 * Uniquely identifies this resource within the server-side container.
	 */
	int handle();

	ItemStack displayStack();

	long count();

	ItemDisplayDelegate EMPTY = new  ItemDisplayDelegate() {
		@Override
		public int handle() {
			return -1;
		}

		@Override
		public ItemStack displayStack() {
			return ItemStack.EMPTY;
		}

		@Override
		public long count() {
			return 0;
		}
	};
}
