package grondag.fermion.gui;

import java.util.Optional;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import grondag.fermion.gui.control.AbstractControl;

public abstract class AbstractContainerScreenWithLayout extends AbstractContainerScreen implements ScreenRenderContext
{
	protected final ContainerLayout layout;
	public static final ContainerLayout LAYOUT;

	static
	{
		LAYOUT = new ContainerLayout();

		LAYOUT.dialogHeight = LAYOUT.externalMargin * 3 + LAYOUT.slotSpacing * 4 + LAYOUT.playerInventoryWidth + AbstractControl.CONTROL_INTERNAL_MARGIN;

		/** distance from top of dialog to start of player inventory area */
		LAYOUT.playerInventoryTop = LAYOUT.dialogHeight - LAYOUT.externalMargin - LAYOUT.slotSpacing * 4 - AbstractControl.CONTROL_INTERNAL_MARGIN;
	}

	public AbstractContainerScreenWithLayout(ContainerLayout layout, Container container, Text title)
	{
		super(container, title);
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


	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		hoverControl = null;
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(hoverControl != null)
		{
			hoverControl.drawToolTip(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public Screen screen()
	{
		return this;
	}

	@Override
	public void setHoverControl(AbstractControl<?> control)
	{
		hoverControl = control;
	}

	@Override
	public void drawToolTip(ItemStack hoverStack, int mouseX, int mouseY)
	{
		renderToolTip(hoverStack, mouseX, mouseY);

	}

	@Override
	public Optional<Element> hoveredElement(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}
	//	@Override
	//	public int mainPanelLeft()
	//	{
	//		return guiLeft + layout.playerInventoryLeft;
	//	}
	//
	//	@Override
	//	public int mainPanelTop()
	//	{
	//		return guiTop + layout.externalMargin;
	//	}
	//
	//	@Override
	//	public int mainPanelSize()
	//	{
	//		return layout.playerInventoryWidth;
	//	}
}
