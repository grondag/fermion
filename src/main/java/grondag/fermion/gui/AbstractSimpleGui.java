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

import grondag.fermion.gui.control.GuiControl;
import grondag.fermion.gui.control.Panel;
import grondag.fermion.gui.control.RenderBounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public abstract class AbstractSimpleGui extends Screen implements GuiRenderContext {

	protected Panel mainPanel;
	protected GuiControl<?> hoverControl;

	protected int guiLeft;
	protected int guiTop;
	protected int guiWidth;
	protected int guiHeight;

	public AbstractSimpleGui() {
		super(new TranslatableText("WUT"));
	}

	@Override
	public void init() {
		super.init();
		guiHeight = height * 4 / 5;
		guiTop = (height - guiHeight) / 2;
		guiWidth = (int) (guiHeight * GuiUtil.GOLDEN_RATIO);
		guiLeft = (width - guiWidth) / 2;
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

		fill(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 0xFFCCCCCC);

		super.render(mouseX, mouseY, partialTicks);

		// Draw controls here because foreground layer is translated to frame of the GUI
		// and our controls are designed to render in frame of the screen.
		// And can't draw after super.drawScreen() because would potentially render on
		// top of things.

		//MachineControlRenderer.setupMachineRendering();
		mainPanel.drawControl(this, mouseX, mouseY, partialTicks);
		//MachineControlRenderer.restoreGUIRendering();

		if (hoverControl != null) {
			hoverControl.drawToolTip(this, mouseX, mouseY, partialTicks);
		}
	}

	//    @Override
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int clickedMouseButton) {
		return mainPanel.mouseClick(minecraft, mouseX, mouseY, clickedMouseButton);
	}

	//    @Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		//        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		//        this.mainPanel.mouseDrag(mc, mouseX, mouseY, clickedMouseButton);
	}

	@Override
	public MinecraftClient minecraft() {
		return minecraft;
	}

	//    @Override
	//    public RenderItem renderItem() {
	//        return this.itemRender;
	//    }
	//
	//    @Override
	//    public GuiScreen screen() {
	//        return this;
	//    }
	//
	//    @Override
	//    public FontRenderer fontRenderer() {
	//        return this.fontRenderer;
	//    }

	@Override
	public void setHoverControl(GuiControl<?> control) {
		hoverControl = control;
	}

	//    @Override
	//    public void drawToolTip(ItemStack hoverStack, int mouseX, int mouseY) {
	//        this.renderToolTip(hoverStack, mouseX, mouseY);

	//    }

	@Override
	public int mainPanelLeft() {
		return guiLeft + guiWidth - GuiControl.CONTROL_EXTERNAL_MARGIN - mainPanelSize();
	}

	@Override
	public int mainPanelTop() {
		return guiTop + GuiControl.CONTROL_EXTERNAL_MARGIN;
	}

	@Override
	public int mainPanelSize() {
		return guiHeight - GuiControl.CONTROL_EXTERNAL_MARGIN * 2;
	}

	protected GuiControl<?> sizeControl(Panel mainPanel, GuiControl<?> machineBufferGauge, RenderBounds<?> spec) {
		// TODO Auto-generated method stub
		return null;
	}
}
