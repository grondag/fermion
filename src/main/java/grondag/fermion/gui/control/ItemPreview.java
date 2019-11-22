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

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemPreview extends AbstractControl<ItemPreview> {
	public ItemPreview(ScreenRenderContext renderContext) {
		super(renderContext);
	}

	public ItemStack previewItem;

	private double contentLeft;
	private double contentTop;
	private double contentSize;

	@Override
	public void drawContent(int mouseX, int mouseY, float partialTicks) {
		if (previewItem != null) {
			GuiUtil.renderItemAndEffectIntoGui(renderContext, previewItem, contentLeft, contentTop, contentSize);
		}
	}

	@Override
	protected void handleCoordinateUpdate() {
		contentSize = Math.min(width, height);
		contentLeft = left + (width - contentSize) / 2;
		contentTop = top + (height - contentSize) / 2;
	}

	@Override
	public void drawToolTip(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub

	}

}