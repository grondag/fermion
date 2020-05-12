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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.spatial.HorizontalAlignment;
import grondag.fermion.spatial.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class BrightnessSlider extends Slider {
	private static final String LABEL = "Brightness";

	public BrightnessSlider(ScreenRenderContext renderContext) {
		super(renderContext, 16, LABEL, 0.22f);
		choiceWidthFactor = 0.1f;
	}

	/** alias for readability */
	public void setBrightness(int brightness) {
		setSelectedIndex(brightness & 0xF);
	}

	/** alias for readability */
	public int getBrightness() {
		return getSelectedIndex();
	}

	@Override
	protected void drawChoice(MatrixStack matrixStack, MinecraftClient mc, ItemRenderer itemRender, float partialTicks) {
		final int color = 0xFFFECE | (((255 * selectedTabIndex / 15) & 0xFF) << 24);

		GuiUtil.drawRect(labelRight, top, labelRight + choiceWidth, bottom, color);

		final int textColor = selectedTabIndex > 6 ? 0xFF000000 : 0xFFFFFFFF;

		GuiUtil.drawAlignedStringNoShadow(matrixStack, mc.textRenderer, Integer.toString(selectedTabIndex), labelRight, top, choiceWidth, height,
				textColor, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
	}

}
