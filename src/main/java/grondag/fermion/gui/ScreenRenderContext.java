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

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import grondag.fermion.gui.control.AbstractControl;

public interface ScreenRenderContext {
	Minecraft minecraft();

	ItemRenderer renderItem();

	Screen screen();

	Font fontRenderer();

	/**
	 * controls that are being hovered over while rendering should call this to
	 * receive a callback after all controls have been rendered to draw a tooltip.
	 */
	void setHoverControl(AbstractControl<?> control);

	default void renderTooltip(PoseStack matrixStack, ItemStack itemStack, int i, int j) {
		screen().renderComponentTooltip(matrixStack, screen().getTooltipFromItem(itemStack), i, j);
	}

	default void drawLocalizedToolTip(PoseStack matrixStack, String lang_key, int mouseX, int mouseY) {
		screen().renderTooltip(matrixStack, new TranslatableComponent(lang_key), mouseX, mouseY);
	}

	default void drawLocalizedToolTip(PoseStack matrixStack, int mouseX, int mouseY, String... lang_keys) {
		if (lang_keys.length == 0) {
			return;
		}

		final ArrayList<Component> list = new ArrayList<>(lang_keys.length);

		for (final String key : lang_keys) {
			list.add(new TranslatableComponent(key));
		}
		screen().renderComponentTooltip(matrixStack, list, mouseX, mouseY);
	}

	default void drawLocalizedToolTipBoolean(PoseStack matrixStack, boolean bool, String true_key, String false_key, int mouseX, int mouseY) {
		screen().renderTooltip(matrixStack, new TranslatableComponent(bool ? true_key : false_key), mouseX, mouseY);
	}

	int screenLeft();

	int screenWidth();

	int screenTop();

	int screenHeight();

	void addControls();
}
