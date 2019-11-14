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

import grondag.fermion.gui.control.GuiControl;
import grondag.fermion.gui.control.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;

public interface GuiRenderContext {
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
	void setHoverControl(GuiControl<?> control);

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

	/**
	 * used by {@link #initGuiContext()} for layout. For containers, set by
	 * container layout. For simple gui, is dynamic to screen size.
	 */
	int mainPanelLeft();

	/**
	 * used by {@link #initGuiContext()} for layout. For containers, set by
	 * container layout. For simple gui, is dynamic to screen size.
	 */
	int mainPanelTop();

	/**
	 * used by {@link #initGuiContext()} for layout. For containers, set by
	 * container layout. For simple gui, is dynamic to screen size.
	 */
	int mainPanelSize();

	//    /**
	//     * Call from initializer to set up main panel and other shared stuff
	//     */
	//    public default Panel initGuiContextAndCreateMainPanel(MachineBlockEntity tileEntity)
	//    {
	//        Panel mainPanel = new Panel(true);
	//        mainPanel.setLayoutDisabled(true);
	//        mainPanel.setLeft(this.mainPanelLeft());
	//        mainPanel.setTop(this.mainPanelTop());
	//        mainPanel.setSquareSize(this.mainPanelSize());
	//        mainPanel.setBackgroundColor(0xFF101010);
	//
	//
	//        mainPanel.add(sizeControl(mainPanel, new MachineName(tileEntity, RenderBounds.BOUNDS_NAME), RenderBounds.BOUNDS_NAME));
	//        mainPanel.add(sizeControl(mainPanel,  new MachineSymbol(tileEntity, RenderBounds.BOUNDS_SYMBOL), RenderBounds.BOUNDS_SYMBOL));
	//
	//
	//        if(tileEntity.clientState().hasOnOff)
	//        {
	//            mainPanel.add(sizeControl(mainPanel, new MachineOnOff(tileEntity, RenderBounds.BOUNDS_ON_OFF), RenderBounds.BOUNDS_ON_OFF));
	//        }
	//
	//        if(tileEntity.clientState().hasRedstoneControl)
	//        {
	//            mainPanel.add(sizeControl(mainPanel, new MachineRedstone(tileEntity, RenderBounds.BOUNDS_REDSTONE), RenderBounds.BOUNDS_REDSTONE));
	//        }
	//
	//        this.addControls(mainPanel, tileEntity);
	//
	//        return mainPanel;
	//    }

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
