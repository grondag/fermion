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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.HorizontalAlignment;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class BrightnessSlider extends Slider {
	// TODO: localize or remove this class
	private static final Component LABEL = new TextComponent("Brightness");

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
	protected void drawChoice(PoseStack matrixStack, Minecraft mc, ItemRenderer itemRender, float partialTicks) {
		final int color = 0xFFFECE | (((255 * selectedTabIndex / 15) & 0xFF) << 24);

		GuiUtil.drawRect(matrixStack.last().pose(), labelRight, top, labelRight + choiceWidth, bottom, color);

		final int textColor = selectedTabIndex > 6 ? 0xFF000000 : 0xFFFFFFFF;

		GuiUtil.drawAlignedStringNoShadow(matrixStack, mc.font, new TextComponent(Integer.toString(selectedTabIndex)), labelRight, top, choiceWidth, height,
				textColor, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
	}

}
