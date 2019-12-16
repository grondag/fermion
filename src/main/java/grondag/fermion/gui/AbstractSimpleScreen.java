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

import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import grondag.fermion.gui.control.AbstractControl;
import grondag.fermion.gui.control.Panel;

public abstract class AbstractSimpleScreen extends Screen implements ScreenRenderContext {

	protected Panel mainPanel;
	protected AbstractControl<?> hoverControl;

	protected int screenLeft;
	protected int screenTop;
	protected int screenWidth;
	protected int screenHeight;

	public AbstractSimpleScreen() {
		super(new LiteralText(""));
	}

	public AbstractSimpleScreen(Text title) {
		super(title);
	}

	@Override
	public void init() {
		super.init();
		screenHeight = height * 4 / 5;
		screenTop = (height - screenHeight) / 2;
		screenWidth = (int) (screenHeight * GuiUtil.GOLDEN_RATIO);
		screenLeft = (width - screenWidth) / 2;
		mainPanel = createMainPanel();
		children.add(mainPanel);
		addControls(mainPanel);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();

		hoverControl = null;
		super.renderBackground();

		fill(screenLeft, screenTop, screenLeft + screenWidth, screenTop + screenHeight, 0xFFCCCCCC);

		// shouldn't do anything but call in case someone is hooking it
		super.render(mouseX, mouseY, partialTicks);

		// Draw controls here because foreground layer is translated to frame of the GUI
		// and our controls are designed to render in frame of the screen.
		// And can't draw after super.drawScreen() because would potentially render on
		// top of things.

		//MachineControlRenderer.setupMachineRendering();
		mainPanel.drawControl(mouseX, mouseY, partialTicks);
		//MachineControlRenderer.restoreGUIRendering();

		if (hoverControl != null) {
			hoverControl.drawToolTip(mouseX, mouseY, partialTicks);
		}
	}

	@Override
	public void addControls(Panel mainPanel) {

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
