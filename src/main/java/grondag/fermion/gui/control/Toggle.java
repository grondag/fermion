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
package grondag.fermion.gui.control;

import grondag.fermion.gui.GuiRenderContext;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.spatial.HorizontalAlignment;
import grondag.fermion.spatial.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class Toggle extends GuiControl<Toggle> {

	protected boolean isOn = false;
	protected String label = "unlabedl toggle";

	protected int targetAreaTop;
	protected int targetAreaBottom;
	protected int labelWidth;
	protected int labelHeight;

	@Override
	protected void drawContent(GuiRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		final float boxRight = (float) (left + labelHeight);

		GuiUtil.drawBoxRightBottom(left, targetAreaTop, boxRight, targetAreaBottom, 1,
			isMouseOver(mouseX, mouseY) ? BUTTON_COLOR_FOCUS : BUTTON_COLOR_ACTIVE);

		if (isOn) {
			GuiUtil.drawRect(left + 2, targetAreaTop + 2, boxRight - 2, targetAreaBottom - 2, BUTTON_COLOR_ACTIVE);
		}

		GuiUtil.drawAlignedStringNoShadow(renderContext.fontRenderer(), label, boxRight + CONTROL_INTERNAL_MARGIN, targetAreaTop, labelWidth,
			labelHeight, TEXT_COLOR_LABEL, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
	}

	@Override
	protected void handleCoordinateUpdate() {
		final int fontHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
		targetAreaTop = (int) Math.max(top, top + (height - fontHeight) / 2);
		targetAreaBottom = (int) Math.min(bottom, targetAreaTop + fontHeight);
		labelHeight = fontHeight;
		labelWidth = MinecraftClient.getInstance().textRenderer.getStringWidth(label);
	}

	@Override
	protected boolean isMouseOver(double mouseX, double mouseY) {
		return !(mouseX < left || mouseX > left + labelHeight + CONTROL_INTERNAL_MARGIN + labelWidth || mouseY < targetAreaTop
			|| mouseY > targetAreaBottom);
	}

	@Override
	public boolean handleMouseClick(MinecraftClient mc, double mouseX, double mouseY, int clickedMouseButton) {
		if (isMouseOver(mouseX, mouseY)) {
			isOn = !isOn;
			GuiUtil.playPressedSound(mc);
		}

		return true;
	}

	@Override
	protected void handleMouseDrag(MinecraftClient mc, int mouseX, int mouseY, int clickedMouseButton) {
		// ignore

	}

	@Override
	protected void handleMouseScroll(int mouseX, int mouseY, int scrollDelta) {
		// ignore
	}

	public boolean isOn() {
		return isOn;
	}

	public Toggle setOn(boolean isOn) {
		this.isOn = isOn;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public Toggle setLabel(String label) {
		this.label = label;
		isDirty = true;
		return this;
	}

	@Override
	public void drawToolTip(GuiRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}

}
