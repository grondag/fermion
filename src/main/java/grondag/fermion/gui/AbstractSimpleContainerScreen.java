package grondag.fermion.gui;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import grondag.fermion.gui.control.AbstractControl;

public abstract class AbstractSimpleContainerScreen<T extends ScreenHandler> extends HandledScreen<T>  implements ScreenRenderContext
{
	protected AbstractControl<?> hoverControl;
	protected final ScreenTheme theme = ScreenTheme.current();


	public AbstractSimpleContainerScreen(T container, PlayerInventory playerInventory, Text title) {
		super(container, playerInventory, title);
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
		y = (height - backgroundHeight) / 2;
		x = (width - backgroundWidth) / 2;
	}

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		super.renderBackground();
		fill(x, y, x + backgroundWidth, y + backgroundHeight, theme.screenBackground);

		final int limit = handler.slots.size();

		// player slot backgrounds
		for(int i = 0; i < limit; i++) {
			final Slot slot = handler.getSlot(i);
			final int u = slot.x + x;
			final int v = slot.y + y;
			fillGradient(u, v, u + theme.itemSize, v + theme.itemSize, theme.itemSlotGradientTop, theme.itemSlotGradientBottom);
		}
	}

	@Override
	public final void render(int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();

		hoverControl = null;

		super.render(mouseX, mouseY, partialTicks);

		RenderSystem.disableRescaleNormal();
		RenderSystem.disableDepthTest();
		RenderSystem.pushMatrix();

		for(int k = 0; k < buttons.size(); ++k) {
			buttons.get(k).render(mouseX, mouseY, partialTicks);
		}

		drawControls(mouseX, mouseY, partialTicks);

		if (hoverControl != null) {
			hoverControl.drawToolTip(mouseX, mouseY, partialTicks);
		}

		RenderSystem.popMatrix();
		RenderSystem.enableDepthTest();
		RenderSystem.enableRescaleNormal();
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		RenderSystem.disableBlend();

		if (focusedSlot != null) {
			final int sx = x + focusedSlot.x;
			final int sy = y + focusedSlot.y;
			GuiUtil.drawBoxRightBottom(sx - theme.itemSelectionMargin, sy - theme.itemSelectionMargin, sx + theme.itemSize + theme.itemSelectionMargin,
					sy + theme.itemSize + theme.itemSelectionMargin, 1, theme.buttonColorFocus);
		}

		//	      this.nameField.render(i, j, f);
		drawMouseoverTooltip(mouseX, mouseY);
	}

	protected abstract void drawControls(int mouseX, int mouseY, float partialTicks);

	@Override
	public void addControls() {

	}

	@Override
	public MinecraftClient minecraft() {
		return client;
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
		return textRenderer;
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
		return x;
	}

	@Override
	public int screenWidth() {
		return backgroundWidth;
	}

	@Override
	public int screenTop() {
		return y;
	}

	@Override
	public int screenHeight() {
		return backgroundHeight;
	}

	@Override
	public Optional<Element> hoveredElement(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}

	// like private vanilla method but doesn't test drawHovereffect for the slot
	public Slot getSlotAt(double x, double y) {
		for(int i = 0; i < handler.slots.size(); ++i) {
			final Slot slot = handler.slots.get(i);

			if (this.isPointOverSlot(slot, x, y)) {
				return slot;
			}
		}

		return null;
	}

	public boolean isPointOverSlot(Slot slot, double x, double y) {
		return isPointWithinBounds(slot.x, slot.y, 16, 16, x, y);
	}
}
