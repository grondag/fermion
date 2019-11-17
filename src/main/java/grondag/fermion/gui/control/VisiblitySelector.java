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

import static grondag.fermion.spatial.HorizontalAlignment.CENTER;
import static grondag.fermion.spatial.VerticalAlignment.MIDDLE;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VisiblitySelector extends AbstractControl<VisiblitySelector> {
	private final VisibilityPanel target;

	private double buttonHeight;

	public VisiblitySelector(ScreenRenderContext renderContext, VisibilityPanel target) {
		super(renderContext);
		this.target = target;
	}

	@Override
	protected void drawContent(int mouseX, int mouseY, float partialTicks) {
		double y = top;

		final int hoverIndex = getButtonIndex(mouseX, mouseY);

		for (int i = 0; i < target.size(); i++) {
			final String label = target.getLabel(i);

			GuiUtil.drawBoxRightBottom(left, y, right, y + buttonHeight, 1, BUTTON_COLOR_ACTIVE);
			final int buttonColor = i == hoverIndex ? BUTTON_COLOR_FOCUS : i == target.getVisiblityIndex() ? BUTTON_COLOR_ACTIVE : BUTTON_COLOR_INACTIVE;
			GuiUtil.drawRect(left + 2, y + 2, right - 2, y + buttonHeight - 2, buttonColor);

			final int textColor = i == hoverIndex ? TEXT_COLOR_FOCUS : i == target.getVisiblityIndex() ? TEXT_COLOR_ACTIVE : TEXT_COLOR_INACTIVE;
			GuiUtil.drawAlignedStringNoShadow(renderContext.fontRenderer(), label, (float) left, (float) y, (float) width, (float) buttonHeight,
				textColor, CENTER, MIDDLE);

			y += buttonHeight;
		}

	}

	private int getButtonIndex(double mouseX, double mouseY) {
		refreshContentCoordinatesIfNeeded();
		if (mouseX < left || mouseX > right || buttonHeight == 0)
			return NO_SELECTION;

		final int selection = (int) ((mouseY - top) / buttonHeight);

		return (selection < 0 || selection >= target.size()) ? NO_SELECTION : selection;
	}

	@Override
	protected void handleCoordinateUpdate() {
		if (target.size() > 0) {
			buttonHeight = height / target.size();
		}
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		final int clickIndex = getButtonIndex(mouseX, mouseY);

		if (clickIndex != NO_SELECTION && clickIndex != target.getVisiblityIndex()) {
			target.setVisiblityIndex(clickIndex);
			GuiUtil.playPressedSound();
		}
	}

	@Override
	public void drawToolTip(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}

}
