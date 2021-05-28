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

import static grondag.fermion.gui.HorizontalAlignment.CENTER;
import static grondag.fermion.gui.VerticalAlignment.MIDDLE;

import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.ScreenTheme;

@Environment(EnvType.CLIENT)
public abstract class Button extends PressableWidget {
	protected final ScreenRenderContext renderContext;
	protected final ScreenTheme theme = ScreenTheme.current();

	public Button(ScreenRenderContext renderContext, int x, int y, int width, int height, Text buttonText) {
		super(x, y, width, height, buttonText);
		this.renderContext = renderContext;
	}

	// TODO: add narration logic
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			final int i = getYImage(hovered);
			final int color = i == 0 ? theme.buttonColorInactive : i == 2 ? theme.buttonColorFocus : theme.buttonColorActive;

			GuiUtil.drawRect(matrixStack.peek().getModel(), x, y, x + width - 1, y + height - 1, color);
			GuiUtil.drawAlignedStringNoShadow(matrixStack, renderContext.fontRenderer(), getMessage(), x, y, width, height, theme.textColorActive, CENTER, MIDDLE);
		}
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder arg) {
		// TODO whatever this is

	}
}
