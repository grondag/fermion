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

import static grondag.fermion.gui.control.GuiControl.BUTTON_COLOR_ACTIVE;
import static grondag.fermion.gui.control.GuiControl.BUTTON_COLOR_FOCUS;
import static grondag.fermion.gui.control.GuiControl.BUTTON_COLOR_INACTIVE;
import static grondag.fermion.gui.control.GuiControl.TEXT_COLOR_ACTIVE;
import static grondag.fermion.spatial.HorizontalAlignment.CENTER;
import static grondag.fermion.spatial.VerticalAlignment.MIDDLE;

import grondag.fermion.gui.GuiUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Environment(EnvType.CLIENT)
public class Button extends AbstractButtonWidget {
	public int buttonColor = BUTTON_COLOR_ACTIVE;
	public int disabledColor = BUTTON_COLOR_INACTIVE;
	public int hoverColor = BUTTON_COLOR_FOCUS;
	public int textColor = TEXT_COLOR_ACTIVE;

	// from 1.12 - not part of 1.14
	protected int buttonId;

	public Button(int buttonId, int x, int y, int width, int height, String buttonText) {
		super(x, y, width, height, buttonText);
		this.buttonId = buttonId;
	}

	public void resize(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	// TODO: add narration logic
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			final int i = getYImage(isHovered);
			final int color = i == 0 ? disabledColor : i == 2 ? hoverColor : buttonColor;

			final MinecraftClient mc = MinecraftClient.getInstance();
			GuiUtil.drawRect(x, y, x + width - 1, y + height - 1, color);
			final TextRenderer fontrenderer = mc.textRenderer;
			GuiUtil.drawAlignedStringNoShadow(fontrenderer, getMessage(), x, y, width, height, textColor, CENTER, MIDDLE);
		}
	}
}
