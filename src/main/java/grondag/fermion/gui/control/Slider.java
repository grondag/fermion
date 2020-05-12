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
import net.minecraft.util.math.MathHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.Layout;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.spatial.HorizontalAlignment;
import grondag.fermion.spatial.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class Slider extends AbstractControl<Slider> {
	public static final int TAB_MARGIN = 2;
	public static final int TAB_WIDTH = 8;
	public static final int ITEM_SPACING = 4;

	protected int size;
	protected String label;

	/** in range 0-1, how much of pixelWidth to allow for label */
	protected float labelWidthFactor = 0;

	/** actual pixelWidth of the label area */
	protected float labelWidth = 0;

	/** point to the right of label area */
	protected float labelRight;

	/** in range 0-1,, how much pixelWidth to allow for drawing selected option */
	protected float choiceWidthFactor = 0;

	/** actual pixelWidth of the selected option area */
	protected float choiceWidth = 0;

	/** size of each tab box, 0 if one continuous bar */
	protected float tabSize;

	/** pixelWidth of area between arrows */
	protected float scrollWidth;

	/**
	 * x point right of choice, left of arrows, tabs. Same as labelRight if no
	 * choice display.
	 */
	protected float choiceRight;

	protected int selectedTabIndex;

	protected enum MouseLocation {
		NONE, CHOICE, LEFT_ARROW, RIGHT_ARROW, TAB
	}

	private MouseLocation currentMouseLocation;
	private int currentMouseIndex;

	/**
	 * Size refers to the number of choices in the slider. Minecraft reference is
	 * needed to set height to font height. labelWidth is in range 0-1 and allows
	 * for alignment of stacked controls.
	 */
	public Slider(ScreenRenderContext renderContext, int size, String label, float labelWidthFactor) {
		super(renderContext);
		this.size = size;
		this.label = label;
		this.labelWidthFactor = labelWidthFactor;
		setHeight(Math.max(TAB_WIDTH, renderContext.fontRenderer().fontHeight + theme.internalMargin));
		setVerticalLayout(Layout.FIXED);
	}

	public void setSize(int size) {
		this.size = size;
	}

	protected void drawChoice(MatrixStack matrixStack, MinecraftClient mc, ItemRenderer itemRender, float partialTicks) {
		// not drawn in base implementation
	}

	@Override
	protected void drawContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (size == 0) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);

		// draw label if there is one
		if (label != null && labelWidth > 0) {
			GuiUtil.drawAlignedStringNoShadow(matrixStack, renderContext.fontRenderer(), label, left, top, labelWidth, height, theme.textColorLabel,
					HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
		}

		if (choiceWidthFactor > 0) {
			drawChoice(matrixStack, renderContext.minecraft(), renderContext.renderItem(), partialTicks);
		}

		// skip drawing tabs if there is only one
		if (size <= 1) {
			return;
		}

		// if tabs are too small, just do a continuous bar
		float tabStartX = choiceRight + TAB_WIDTH + ITEM_SPACING;
		final float tabTop = top + (height - TAB_WIDTH) / 2;
		final float tabBottom = tabTop + TAB_WIDTH;
		if (tabSize == 0.0) {
			GuiUtil.drawRect(tabStartX, tabTop, tabStartX + scrollWidth, tabBottom, theme.buttonColorInactive);

			// box pixelWidth is same as tab height, so need to have it be half that extra
			// to the right so that we keep our margins with the arrows
			final float selectionCenterX = tabStartX + TAB_WIDTH * 0.5f + (scrollWidth - TAB_WIDTH) * selectedTabIndex / (size - 1);

			GuiUtil.drawRect(selectionCenterX - TAB_WIDTH * 0.5f, tabTop, selectionCenterX - TAB_WIDTH * 0.5f, tabBottom, theme.buttonColorActive);
		} else {
			final int highlightIndex = currentMouseLocation == MouseLocation.TAB ? currentMouseIndex : -1;

			for (int i = 0; i < size; i++) {
				GuiUtil.drawRect(tabStartX, tabTop, tabStartX + tabSize, tabBottom,
						i == highlightIndex ? theme.buttonColorFocus : i == selectedTabIndex ? theme.buttonColorActive : theme.buttonColorInactive);
				tabStartX += (tabSize + TAB_MARGIN);
			}
		}

		final float arrowCenterY = tabTop + TAB_WIDTH * 0.5f;

		GuiUtil.drawQuad(choiceRight, arrowCenterY, choiceRight + TAB_WIDTH, tabBottom, choiceRight + TAB_WIDTH, tabTop, choiceRight,
				arrowCenterY, currentMouseLocation == MouseLocation.LEFT_ARROW ? theme.buttonColorFocus : theme.buttonColorInactive);

		GuiUtil.drawQuad(right, arrowCenterY, right - TAB_WIDTH, tabTop, right - TAB_WIDTH, tabBottom, right, arrowCenterY,
				currentMouseLocation == MouseLocation.RIGHT_ARROW ? theme.buttonColorFocus : theme.buttonColorInactive);

	}

	private void updateMouseLocation(double mouseX, double mouseY) {
		if (size == 0) {
			return;
		}

		if (mouseX < choiceRight || mouseX > right || mouseY < top || mouseY > top + TAB_WIDTH) {
			currentMouseLocation = MouseLocation.NONE;
		} else if (mouseX <= choiceRight + TAB_WIDTH + ITEM_SPACING / 2.0) {
			currentMouseLocation = MouseLocation.LEFT_ARROW;
		} else if (mouseX >= right - TAB_WIDTH - ITEM_SPACING / 2.0) {
			currentMouseLocation = MouseLocation.RIGHT_ARROW;
		} else {
			currentMouseLocation = MouseLocation.TAB;
			currentMouseIndex = MathHelper.clamp((int) ((mouseX - choiceRight - TAB_WIDTH - ITEM_SPACING / 2) / (scrollWidth) * size), 0,
					size - 1);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (size != 0) {
			labelWidth = width * labelWidthFactor;
			choiceWidth = width * choiceWidthFactor;
			labelRight = left + labelWidth;
			choiceRight = labelRight + choiceWidth + theme.internalMargin;
			scrollWidth = width - labelWidth - choiceWidth - theme.internalMargin - (TAB_WIDTH + ITEM_SPACING) * 2;
			tabSize = size <= 0 ? 0 : (scrollWidth - (TAB_MARGIN * (size - 1))) / size;
			if (tabSize < TAB_MARGIN * 2) {
				tabSize = 0;
			}
		}
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (size == 0) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);
		switch (currentMouseLocation) {
		case LEFT_ARROW:
			if (selectedTabIndex > 0) {
				selectedTabIndex--;
			}
			GuiUtil.playPressedSound();
			break;

		case RIGHT_ARROW:
			if (selectedTabIndex < size - 1) {
				selectedTabIndex++;
			}
			GuiUtil.playPressedSound();
			break;

		case TAB:
			selectedTabIndex = currentMouseIndex;
			break;

		case NONE:
		default:
			break;

		}
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		if (size == 0) {
			return;
		}

		updateMouseLocation(mouseX, mouseY);
		if (currentMouseLocation == MouseLocation.TAB) {
			selectedTabIndex = currentMouseIndex;
		}
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		if (size == 0) {
			return;
		}

		selectedTabIndex = MathHelper.clamp(selectedTabIndex + mouseIncrementDelta(), 0, size - 1);
	}

	public int size() {
		return size;
	}

	public void setSelectedIndex(int index) {
		selectedTabIndex = size == 0 ? NO_SELECTION : MathHelper.clamp(index, 0, size - 1);
	}

	public int getSelectedIndex() {
		return size == 0 ? NO_SELECTION : selectedTabIndex;
	}

	@Override
	public void drawToolTip(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}

}
