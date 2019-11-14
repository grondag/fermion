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

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import grondag.fermion.gui.GuiRenderContext;
import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.container.ItemDisplayDelegate;
import grondag.fermion.spatial.HorizontalAlignment;
import grondag.fermion.spatial.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemStackPicker extends TabBar<ItemDisplayDelegate> {
	protected final TextRenderer fontRenderer;
	protected final MouseHandler<ItemDisplayDelegate> clickHandler;

	public ItemStackPicker(List<ItemDisplayDelegate> items, TextRenderer fontRenderer, MouseHandler<ItemDisplayDelegate> clickHandler) {
		super(items);
		this.fontRenderer = fontRenderer;
		this.clickHandler = clickHandler;
		setItemsPerRow(9);
		setItemSpacing(2);
		setItemSelectionMargin(1);
		setSelectionEnabled(false);
		setCaptionHeight(fontRenderer.fontHeight * 6 / 10 + 4);
	}

	@Override
	protected void drawContent(GuiRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		super.drawContent(renderContext, mouseX, mouseY, partialTicks);
	}

	private String getQuantityLabel(long qty) {
		if (qty < 1000)
			return Long.toString(qty);
		else if (qty < 10000)
			return String.format("%.1fK", (float) qty / 1000);
		else if (qty < 100000)
			return Long.toString(qty / 1000) + "K";
		else
			return "many";
	}

	@Override
	protected void drawItem(ItemDisplayDelegate item, MinecraftClient mc, ItemRenderer itemRender, double left, double top, float partialTicks,
		boolean isHighlighted) {
		final int x = (int) left;
		final int y = (int) top;

		@SuppressWarnings("unused")
		final
		ItemStack stack = item.displayStack();
		GlStateManager.enableLighting();
		//TODO: reimplement
		//        itemRender.renderItemAndGlintIntoGUI(stack, x, y);
		//        itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, "");

		// itemRender doesn't clean this up, messes up highlight boxes
		drawQuantity(item.count(), x, y);
	}

	protected void drawQuantity(long qty, int left, int top) {
		if (qty < 2)
			return;

		final String qtyLabel = getQuantityLabel(qty);
		//TODO: reimplement
		//        boolean wasUnicode = this.fontRenderer.getUnicodeFlag();
		//        if (wasUnicode)
		//            this.fontRenderer.setUnicodeFlag(false);
		//
		//        GlStateManager.pushMatrix();
		//        GlStateManager.disableLighting();
		//        GlStateManager.disableDepth();
		//        GlStateManager.disableBlend();
		//
		//        GlStateManager.translate(left, top + 18, 0);
		//        GlStateManager.scale(0.6, 0.6, 1);

		GuiUtil.drawAlignedStringNoShadow(fontRenderer, qtyLabel, 0, 0, 16 * 10 / 6, getCaptionHeight() * 10 / 6, 0xFFFFFFFF,
			HorizontalAlignment.CENTER, VerticalAlignment.TOP);

		//        GlStateManager.enableDepth();
		//        GlStateManager.enableBlend();
		GlStateManager.popMatrix();

		//TODO: reimplement
		//        if (wasUnicode)
		//            this.fontRenderer.setUnicodeFlag(true);
	}

	@Override
	protected void setupItemRendering() {
		//TODO: reimplement
		//        GlStateManager.enableDepthTest();
		//        GlStateManager.enableBlend();
		//        RenderHelper.enableGUIStandardItemLighting();
		//        GlStateManager.enableRescaleNormal();
		//        GLX.setLightmapTextureCoords(GLX.lightmapTexUnit, 240.0F, 240.0F);
		//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean handleMouseClick(MinecraftClient mc, double mouseX, double mouseY, int clickedMouseButton) {

		if (clickHandler != null && currentMouseLocation == MouseLocation.ITEM) {
			clickHandler.handle(mc, clickedMouseButton, resourceForClickHandler());
			return true;
		} else {
			return super.handleMouseClick(mc, mouseX, mouseY, clickedMouseButton);
		}
	}

	private ItemDisplayDelegate resourceForClickHandler() {
		final ItemDisplayDelegate res = get(currentMouseIndex);
		return res == null ? ItemDisplayDelegate.EMPTY : res;
	}

	@Override
	protected void drawToolTip(ItemDisplayDelegate item, GuiRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		renderContext.drawToolTip(item.displayStack(), mouseX, mouseY);

	}
}
