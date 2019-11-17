package grondag.fermion.gui;

import java.io.IOException;

import grondag.fermion.gui.control.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;

/**
 * Dummy for missing 1.12 class
 */
public abstract class GuiContainer extends Screen {

	protected int xSize;

	protected int ySize;

	protected int guiLeft;
	protected int guiRight;
	protected int guiTop;

	protected MinecraftClient mc;
	protected ItemRenderer itemRender;
	protected TextRenderer fontRenderer;
	protected Container inventorySlots;

	public GuiContainer(Container container) {
		super(null);
		inventorySlots = container;
		// TODO Auto-generated constructor stub
	}

	public void initGui() {
		// TODO Auto-generated method stub

	}

	protected <T> Panel initScreenContextAndCreateMainPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean doesGuiPauseGame() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	protected void drawRect(int guiLeft, int guiTop, int i, int j, int k) {
		// TODO Auto-generated method stub

	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}

	public void drawDefaultBackground() {
		// TODO Auto-generated method stub

	}

	public void renderHoveredToolTip(int mouseX, int mouseY) {
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
