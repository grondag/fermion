package grondag.fermion.gui.control;

import javax.annotation.Nonnull;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.container.ItemDisplayDelegate;
import grondag.fermion.gui.container.OpenContainerStorageInteractionC2S;
import grondag.fermion.gui.container.OpenContainerStorageInteractionC2S.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

public class ItemDisplayClickHandler implements MouseHandler<ItemDisplayDelegate> {
	public static final ItemDisplayClickHandler INSTANCE = new ItemDisplayClickHandler();

	private ItemDisplayClickHandler() {
	}

	@Override
	public void handle(MinecraftClient mc, int mouseButton, @Nonnull ItemDisplayDelegate target) {
		Action action = null;

		final boolean isShift = Screen.hasShiftDown();

		final ItemStack heldStack = mc.player.inventory.getMainHandStack();

		// if alt/right/middle clicking on same bulkResource, don't count that as a
		// deposit
		if (heldStack != null && !heldStack.isEmpty()
			&& !(ItemHelper.canStacksCombine(heldStack, target.displayStack()) && (Screen.hasAltDown() || mouseButton > 0))) {
			// putting something in
			if (mouseButton == GuiUtil.MOUSE_LEFT && !Screen.hasAltDown()) {
				action = Action.PUT_ALL_HELD;
			} else {
				action = Action.PUT_ONE_HELD;
			}
		} else {
			if (mouseButton == GuiUtil.MOUSE_LEFT && !Screen.hasAltDown()) {
				action = isShift ? Action.QUICK_MOVE_STACK : Action.TAKE_STACK;
			} else if (mouseButton == GuiUtil.MOUSE_MIDDLE || Screen.hasAltDown()) {
				action = isShift ? Action.QUICK_MOVE_ONE : Action.TAKE_ONE;
			} else if (mouseButton == GuiUtil.MOUSE_RIGHT) {
				action = isShift ? Action.QUICK_MOVE_HALF : Action.TAKE_HALF;
			}
		}

		if (action != null) {
			OpenContainerStorageInteractionC2S.sendPacket(action, target);
		}
	}
}