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
import java.util.List;

import grondag.fermion.gui.control.AbstractControl;
import grondag.fermion.gui.control.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;

public interface ScreenRenderContext {
	int CAPACITY_BAR_WIDTH = 4;

	MinecraftClient minecraft();

	ItemRenderer renderItem();

	Screen screen();

	TextRenderer fontRenderer();

	void drawToolTip(ItemStack hoverStack, int mouseX, int mouseY);

	/**
	 * controls that are being hovered over while rendering should call this to
	 * receive a callback after all controls have been rendered to draw a tooltip.
	 */
	void setHoverControl(AbstractControl<?> control);

	/**
	 * Draws the given text as a tooltip.
	 */
	default void drawToolTip(String text, int mouseX, int mouseY) {
		screen().renderTooltip(text, mouseX, mouseY);
	}

	default void drawToolTip(List<String> textLines, int mouseX, int mouseY) {
		screen().renderTooltip(textLines, mouseX, mouseY);
	}

	default void drawLocalizedToolTip(String lang_key, int mouseX, int mouseY) {
		this.drawToolTip(I18n.translate(lang_key), mouseX, mouseY);
	}

	default void drawLocalizedToolTip(int mouseX, int mouseY, String... lang_keys) {
		if (lang_keys.length == 0)
			return;

		final ArrayList<String> list = new ArrayList<String>(lang_keys.length);

		for (final String key : lang_keys) {
			list.add(I18n.translate(key));
		}
		this.drawToolTip(list, mouseX, mouseY);
	}

	default void drawLocalizedToolTipBoolean(boolean bool, String true_key, String false_key, int mouseX, int mouseY) {
		this.drawToolTip(I18n.translate(bool ? true_key : false_key), mouseX, mouseY);
	}

	int screenLeft();

	int screenWidth();

	int screenTop();

	int screenHeight();

	default Panel createMainPanel() {
		final Panel mainPanel = new Panel(this, true);
		mainPanel.setLeft(screenLeft() + AbstractControl.CONTROL_EXTERNAL_MARGIN);
		mainPanel.setTop(screenTop() + AbstractControl.CONTROL_EXTERNAL_MARGIN);
		mainPanel.setWidth(screenWidth() - AbstractControl.CONTROL_EXTERNAL_MARGIN * 2);
		mainPanel.setHeight(screenHeight() - AbstractControl.CONTROL_EXTERNAL_MARGIN * 2);
		mainPanel.setBackgroundColor(0x00FFFFFF);
		return mainPanel;
	}

	//    public default AbstractMachineControl<?, ?> sizeControl(Panel mainPanel, AbstractMachineControl<?, ?> control, AbstractRectRenderBounds bounds)
	//    {
	//        control.setLeft(mainPanel.getLeft() + mainPanel.getWidth() * ((AbstractRectRenderBounds)bounds).left());
	//        control.setTop(mainPanel.getTop() + mainPanel.getHeight() * ((AbstractRectRenderBounds)bounds).top());
	//        control.setWidth(mainPanel.getWidth() * ((AbstractRectRenderBounds)bounds).width());
	//        control.setHeight(mainPanel.getHeight() * ((AbstractRectRenderBounds)bounds).height());
	//        return control;
	//    }

	void addControls(Panel mainPanel);
}
