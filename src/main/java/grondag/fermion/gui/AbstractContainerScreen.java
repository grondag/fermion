package grondag.fermion.gui;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.text.Text;

import net.fabricmc.loader.api.FabricLoader;

public abstract class AbstractContainerScreen extends AbstractSimpleScreen
{
	protected Container inventorySlots;
	protected final ContainerLayout layout;

	public AbstractContainerScreen(ContainerLayout layout, Container container, Text title) {
		super(title);
		inventorySlots = container;
		this.layout = layout;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	protected void computeScreenBounds() {
		super.computeScreenBounds();
		screenWidth = layout.dialogWidth;
		screenHeight = layout.dialogHeight;

		screenTop = (height - screenHeight) / 2;

		// if using REI, center on left 2/3 of screen to allow more room for REI
		if(FabricLoader.getInstance().isModLoaded("rei")) {
			screenLeft = ((width * 2 / 3) - screenWidth) / 2;
		} else {
			screenLeft = (width - screenWidth) / 2;
		}
	}

	@Override
	public void renderBackground() {
		super.renderBackground();

		// player slot backgrounds
		for(final Slot slot : inventorySlots.slotList) {
			final int x = screenLeft + slot.xPosition;
			final int y = screenTop + slot.yPosition;
			fillGradient(x, y, x + 16, y + 16, 0xFFA9A9A9, 0xFF898989);
		}
	}

	@Override
	protected void drawControls(int mouseX, int mouseY, float partialTicks) {
		super.drawControls(mouseX, mouseY, partialTicks);
	}
}
