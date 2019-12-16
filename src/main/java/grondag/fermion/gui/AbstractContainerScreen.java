package grondag.fermion.gui;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.text.Text;

import grondag.fermion.gui.control.AbstractControl;

public abstract class AbstractContainerScreen extends AbstractSimpleScreen
{
	protected int xSize;

	protected int ySize;

	protected int guiLeft;
	protected int guiRight;
	protected int guiTop;

	protected Container inventorySlots;
	protected final ContainerLayout layout;

	public static final ContainerLayout LAYOUT;

	static {
		LAYOUT = new ContainerLayout();

		LAYOUT.dialogHeight = LAYOUT.externalMargin * 3 + LAYOUT.slotSpacing * 4 + LAYOUT.playerInventoryWidth + AbstractControl.CONTROL_INTERNAL_MARGIN;

		/** distance from top of dialog to start of player inventory area */
		LAYOUT.playerInventoryTop = LAYOUT.dialogHeight - LAYOUT.externalMargin - LAYOUT.slotSpacing * 4 - AbstractControl.CONTROL_INTERNAL_MARGIN;
	}

	public AbstractContainerScreen(ContainerLayout layout, Container container, Text title) {
		super(title);
		inventorySlots = container;
		this.layout = layout;
		xSize = layout.dialogWidth;
		ySize = layout.dialogHeight;
	}

	@Override
	public void renderBackground() {
		super.renderBackground();

		GuiUtil.drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xFFCCCCCC);

		// player slot backgrounds
		for(final Slot slot : inventorySlots.slotList)
		{
			final int x = guiLeft + slot.xPosition;
			final int y = guiTop + slot.yPosition;
			GuiUtil.drawGradientRect(x, y, x + 16, y + 16, 0xFFA9A9A9, 0xFF898989);
		}
	}
}
