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

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.HorizontalAlignment;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class Toggle extends AbstractControl<Toggle> {
	public Toggle(ScreenRenderContext renderContext) {
		super(renderContext);
	}

	protected boolean isOn = false;
	protected Component label = new TextComponent("yes?");

	protected int targetAreaTop;
	protected int targetAreaBottom;
	protected int labelWidth;
	protected int labelHeight;

	protected BooleanConsumer onChanged = b -> {};

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final float boxRight = left + labelHeight;

		GuiUtil.drawBoxRightBottom(matrixStack.last().pose(), left, targetAreaTop, boxRight, targetAreaBottom, 1,
				isMouseOver(mouseX, mouseY) ? theme.buttonColorFocus : theme.buttonColorActive);

		if (isOn) {
			GuiUtil.drawRect(matrixStack.last().pose(), left + 2, targetAreaTop + 2, boxRight - 2, targetAreaBottom - 2, theme.buttonColorActive);
		}

		GuiUtil.drawAlignedStringNoShadow(matrixStack, renderContext.fontRenderer(), label, boxRight + theme.internalMargin, targetAreaTop, labelWidth,
				height, theme.textColorLabel, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
	}

	@SuppressWarnings("resource")
	@Override
	protected void handleCoordinateUpdate() {
		final int fontHeight = Minecraft.getInstance().font.lineHeight;
		targetAreaTop = (int) Math.max(top, top + (height - fontHeight) / 2);
		targetAreaBottom = (int) Math.min(bottom, targetAreaTop + fontHeight);
		labelHeight = fontHeight;
		labelWidth = Minecraft.getInstance().font.width(label);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return !(mouseX < left || mouseX > left + theme.internalMargin + labelHeight + labelWidth || mouseY < targetAreaTop
				|| mouseY > targetAreaBottom);
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (isMouseOver(mouseX, mouseY)) {
			isOn = !isOn;
			GuiUtil.playPressedSound();
			onChanged.accept(isOn);
		}
	}

	public void onChanged(BooleanConsumer onChanged) {
		this.onChanged = onChanged;
	}

	public boolean isOn() {
		return isOn;
	}

	public Toggle setOn(boolean isOn) {
		this.isOn = isOn;
		return this;
	}

	public Component getLabel() {
		return label;
	}

	public Toggle setLabel(Component label) {
		this.label = label;
		isDirty = true;
		return this;
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}
}
