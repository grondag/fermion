/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.fermion.gui;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import grondag.fermion.gui.control.AbstractControl;

public abstract class AbstractSimpleScreen extends Screen implements ScreenRenderContext {

	protected final ScreenTheme theme = ScreenTheme.current();
	protected AbstractControl<?> hoverControl;

	protected int screenLeft;
	protected int screenTop;
	protected int screenWidth;
	protected int screenHeight;

	public AbstractSimpleScreen() {
		super(new TextComponent(""));
	}

	public AbstractSimpleScreen(Component title) {
		super(title);
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
		screenHeight = height * 4 / 5;
		screenTop = (height - screenHeight) / 2;
		screenWidth = (int) (screenHeight * GuiUtil.GOLDEN_RATIO);
		screenLeft = (width - screenWidth) / 2;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public final void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();

		hoverControl = null;

		renderBackground(matrixStack);

		// shouldn't do anything but call in case someone is hooking it
		super.render(matrixStack, mouseX, mouseY, partialTicks);

		drawControls(matrixStack, mouseX, mouseY, partialTicks);

		if (hoverControl != null) {
			hoverControl.drawToolTip(matrixStack, mouseX, mouseY, partialTicks);
		}
	}

	protected void drawControls(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final List<? extends GuiEventListener> children = children();
		final int limit = children.size();

		for (int i = 0; i < limit; ++i) {
			final GuiEventListener e = children.get(i);

			if (e instanceof AbstractControl) {
				((AbstractControl<?>) children.get(i)).render(matrixStack, mouseX, mouseY, partialTicks);
			}
		}
	}

	@Override
	public void addControls() {

	}

	@Override
	public Minecraft minecraft() {
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
	public Font fontRenderer() {
		return font;
	}

	@Override
	public void setHoverControl(AbstractControl<?> control) {
		hoverControl = control;
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
	public Optional<GuiEventListener> getChildAt(double double_1, double double_2) {
		return Optional.ofNullable(hoverControl);
	}

	@Override
	public void renderTooltip(PoseStack matrixStack, ItemStack itemStack, int i, int j) {
		super.renderTooltip(matrixStack,  itemStack, i, j);
	}
}
