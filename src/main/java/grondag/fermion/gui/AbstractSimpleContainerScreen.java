package grondag.fermion.gui;

import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import net.fabricmc.loader.api.FabricLoader;

import grondag.fermion.gui.control.AbstractControl;

public abstract class AbstractSimpleContainerScreen<T extends Container> extends AbstractContainerScreen<T>  implements ScreenRenderContext
{
	protected AbstractControl<?> hoverControl;

	protected int screenLeft;
	protected int screenTop;
	protected int screenWidth;
	protected int screenHeight;

	protected final ContainerLayout layout;

	public AbstractSimpleContainerScreen(ContainerLayout layout, T container, PlayerInventory playerInventory, Text title) {
		super(container, playerInventory, title);
		this.layout = layout;
	}

	@Override
	public void init() {
		super.init();
		computeScreenBounds();
		addControls();
	}

	/**
	 * Called during init before controls are created.
	 */
	protected void computeScreenBounds() {
		screenWidth = layout.dialogWidth;
		screenHeight = layout.dialogHeight;

		screenTop = (height - screenHeight) / 2;

		// if using REI, center on left 2/3 of screen to allow more room for REI
		if(FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			screenLeft = ((width * 2 / 3) - screenWidth) / 2;
		} else {
			screenLeft = (width - screenWidth) / 2;
		}
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		fill(screenLeft, screenTop, screenLeft + screenWidth, screenTop + screenHeight, 0xFFCCCCCC);

		final Slot upperLeft = container.getSlot(9);

		final int offsetX = screenLeft - upperLeft.xPosition + layout.playerInventoryLeft;
		final int offsetY = screenTop - upperLeft.yPosition + layout.playerInventoryTop;

		final int limit = container.slotList.size() - 1;
		// player slot backgrounds
		for(int i = 9; i < limit; i++) {
			final Slot slot = container.getSlot(i);
			final int x = offsetX + slot.xPosition;
			final int y = offsetY + slot.yPosition;
			fillGradient(x, y, x + 16, y + 16, 0xFFA9A9A9, 0xFF898989);
		}
	}

	@Override
	public final void render(int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();

		hoverControl = null;

		renderBackground();

		// Don't call super render because will render container crap
		for(int k = 0; k < buttons.size(); ++k) {
			buttons.get(k).render(mouseX, mouseY, partialTicks);
		}

		drawControls(mouseX, mouseY, partialTicks);

		if (hoverControl != null) {
			hoverControl.drawToolTip(mouseX, mouseY, partialTicks);
		}
	}

	protected abstract void drawControls(int mouseX, int mouseY, float partialTicks);

	@Override
	public void addControls() {

	}

	@Override
	public MinecraftClient minecraft() {
		return minecraft;
	}

	@Override
	public ItemRenderer renderItem() {
		return itemRenderer;
	}

	@Override
	public Screen screen() {
		return this;
	}

	@Override
	public TextRenderer fontRenderer() {
		return font;
	}

	@Override
	public void setHoverControl(AbstractControl<?> control) {
		hoverControl = control;
	}

	@Override
	public void drawToolTip(ItemStack hoverStack, int mouseX, int mouseY) {
		super.renderTooltip(hoverStack, mouseX, mouseY);
	}

	@Override
	public int screenLeft() {
		return screenLeft;
	}

	@Override
	public int screenWidth() {
		return screenWidth;
	}

	@Override
	public int screenTop() {
		return screenTop;
	}

	@Override
	public int screenHeight() {
		return screenHeight;
	}

	@Override
	public Optional<Element> hoveredElement(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}
}
