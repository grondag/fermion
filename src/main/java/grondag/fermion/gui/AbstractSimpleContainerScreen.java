package grondag.fermion.gui;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;

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
		y = (height - containerHeight) / 2;

		// if using REI, center on left 2/3 of screen to allow more room for REI
		if(FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
			x = ((width * 2 / 3) - containerWidth) / 2;
		} else {
			x = (width - containerWidth) / 2;
		}
	}

	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		super.renderBackground();
		fill(x, y, x + containerWidth, y + containerHeight, theme.screenBackground);

		final int limit = container.slotList.size();

		// player slot backgrounds
		for(int i = 0; i < limit; i++) {
			final Slot slot = container.getSlot(i);
			final int u = slot.xPosition + x;
			final int v = slot.yPosition + y;
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
		return x;
	}

	@Override
	public int screenWidth() {
		return containerWidth;
	}

	@Override
	public int screenTop() {
		return y;
	}

	@Override
	public int screenHeight() {
		return containerHeight;
	}

	@Override
	public Optional<Element> hoveredElement(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}
}