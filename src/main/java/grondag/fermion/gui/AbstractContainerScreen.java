package grondag.fermion.gui;

import java.io.IOException;

import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Dummy for missing 1.12 class
 */
public abstract class AbstractContainerScreen extends AbstractSimpleScreen {

	protected int xSize;

	protected int ySize;

	protected int guiLeft;
	protected int guiRight;
	protected int guiTop;

	protected Container inventorySlots;

	public AbstractContainerScreen(Container container, Text title) {
		super(title);
		inventorySlots = container;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}

	protected void mouseClicked(int mouseX, int mouseY, int clickedMouseButton) throws IOException {

	}

	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		// TODO Auto-generated method stub

	}

	protected void renderToolTip(ItemStack hoverStack, int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	protected void drawGradientRect(int x, int y, int i, int j, int k, int l) {
		// TODO Auto-generated method stub

	}
}
