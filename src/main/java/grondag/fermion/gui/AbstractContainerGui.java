package grondag.fermion.gui;

import java.io.IOException;

import javax.annotation.Nullable;

import grondag.fermion.gui.control.GuiControl;
import grondag.fermion.gui.control.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

public abstract class AbstractContainerGui extends GuiContainer implements GuiRenderContext
{
	protected final ContainerLayout layout;

	public static final ContainerLayout LAYOUT;


	protected @Nullable Panel mainPanel;
	protected @Nullable GuiControl<?> hoverControl;

	static
	{
		LAYOUT = new ContainerLayout();

		LAYOUT.dialogHeight = LAYOUT.externalMargin * 3 + LAYOUT.slotSpacing * 4 + LAYOUT.playerInventoryWidth + GuiControl.CONTROL_INTERNAL_MARGIN;

		/** distance from top of dialog to start of player inventory area */
		LAYOUT.playerInventoryTop = LAYOUT.dialogHeight - LAYOUT.externalMargin - LAYOUT.slotSpacing * 4 - GuiControl.CONTROL_INTERNAL_MARGIN;
	}

	public AbstractContainerGui(ContainerLayout layout, Container container)
	{
		super(container);
		this.layout = layout;
		xSize = layout.dialogWidth;
		ySize = layout.dialogHeight;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		// if using JEI, center on left 2/3 of screen to allow more room for JEI
		//        if(Loader.instance().getIndexedModList().containsKey("jei"))
		//        {
		//            this.guiLeft = ((this.width * 2 / 3) - this.xSize) / 2;
		//        }

		mainPanel = this.initGuiContextAndCreateMainPanel();

	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xFFCCCCCC);

		// player slot backgrounds
		for(final Slot slot : inventorySlots.slotList)
		{
			final int x = guiLeft + slot.xPosition;
			final int y = guiTop + slot.yPosition;
			drawGradientRect(x, y, x + 16, y + 16, 0xFFA9A9A9, 0xFF898989);
		}

		// Draw controls here because foreground layer is translated to frame of the GUI
		// and our controls are designed to render in frame of the screen.
		// And can't draw after super.drawScreen() because would potentially render on top of things.

		//		MachineControlRenderer.setupMachineRendering();
		mainPanel.drawControl(this, mouseX, mouseY, partialTicks);
		//		MachineControlRenderer.restoreGUIRendering();


	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		hoverControl = null;
		super.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		super.renderHoveredToolTip(mouseX, mouseY);
		if(hoverControl != null)
		{
			hoverControl.drawToolTip(this, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int clickedMouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, clickedMouseButton);
		mainPanel.mouseClick(mc, mouseX, mouseY, clickedMouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
	{
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		mainPanel.mouseDrag(mc, mouseX, mouseY, clickedMouseButton);
	}

	@Override
	public MinecraftClient minecraft()
	{
		return mc;
	}

	@Override
	public ItemRenderer renderItem()
	{
		return itemRender;
	}

	@Override
	public Screen screen()
	{
		return this;
	}

	@Override
	public TextRenderer fontRenderer()
	{
		return fontRenderer;
	}

	@Override
	public void setHoverControl(GuiControl<?> control)
	{
		hoverControl = control;
	}

	@Override
	public void drawToolTip(ItemStack hoverStack, int mouseX, int mouseY)
	{
		renderToolTip(hoverStack, mouseX, mouseY);

	}

	@Override
	public int mainPanelLeft()
	{
		return guiLeft + layout.playerInventoryLeft;
	}

	@Override
	public int mainPanelTop()
	{
		return guiTop + layout.externalMargin;
	}

	@Override
	public int mainPanelSize()
	{
		return layout.playerInventoryWidth;
	}
}
