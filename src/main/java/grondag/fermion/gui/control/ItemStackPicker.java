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

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.container.ItemDisplayDelegate;

@Environment(EnvType.CLIENT)
public class ItemStackPicker<T extends ItemDisplayDelegate> extends TabBar<T> {
	protected final TextRenderer fontRenderer;
	protected final MouseHandler<T> clickHandler;

	public ItemStackPicker(ScreenRenderContext renderContext, List<T> items, TextRenderer fontRenderer, MouseHandler<T> clickHandler) {
		super(renderContext, items);
		this.fontRenderer = fontRenderer;
		this.clickHandler = clickHandler;
		setItemsPerRow(9);
		setItemSpacing(2);
		setItemSelectionMargin(1);
		setSelectionEnabled(false);
		setCaptionHeight(fontRenderer.fontHeight * 6 / 10 + 4);
	}

	@Override
	protected void drawContent(int mouseX, int mouseY, float partialTicks) {
		super.drawContent(mouseX, mouseY, partialTicks);
	}

	private String getQuantityLabel(long qty) {
		if (qty < 1000) {
			return Long.toString(qty);
		} else if (qty < 10000) {
			return String.format("%.1fK", (float) qty / 1000);
		} else if (qty < 100000) {
			return Long.toString(qty / 1000) + "K";
		} else {
			return "many";
		}
	}

	@Override
	protected void drawItem(ItemDisplayDelegate item, MinecraftClient mc, ItemRenderer itemRenderer, double left, double top, float partialTicks,
			boolean isHighlighted) {
		final int x = (int) left;
		final int y = (int) top;

		final ItemStack itemStack = item.displayStack();

		setBlitOffset(200);
		itemRenderer.zOffset = 200.0F;

		GuiUtil.renderItemAndEffectIntoGui(mc, itemRenderer, itemStack, x, y, actualItemPixels());
		// TODO: support for dragging

		drawQuantity(item.count(), x, y);

		setBlitOffset(0);
		itemRenderer.zOffset = 0.0F;
	}

	protected void drawQuantity(long qty, int left, int top) {
		if (qty < 2) {
			return;
		}

		final float scale = actualItemPixels() / 16f;

		final String qtyLabel = getQuantityLabel(qty);
		// PERF: use a fixed instance
		final MatrixStack matrixStack = new MatrixStack();
		matrixStack.translate(0.0D, 0.0D, 200.0F);
		final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		final float x = left + 8 * scale - fontRenderer.getStringWidth(qtyLabel) * 0.5f;
		final float y = top + 16 * scale;
		fontRenderer.draw(qtyLabel, x, y, 0xFF000000, false, matrixStack.peek().getModel(), immediate, true, 0, 15728880);
		immediate.draw();

	}

	@Override
	protected void setupItemRendering() {
		RenderSystem.disableDepthTest();
		RenderSystem.enableRescaleNormal();
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {

		if (clickHandler != null && currentMouseLocation == MouseLocation.ITEM) {
			clickHandler.handle(MinecraftClient.getInstance(), clickedMouseButton, resourceForClickHandler());
		} else {
			super.handleMouseClick(mouseX, mouseY, clickedMouseButton);
		}
	}

	@SuppressWarnings("unchecked")
	private T resourceForClickHandler() {
		final T res = get(currentMouseIndex);
		return res == null ? (T) ItemDisplayDelegate.EMPTY : res;
	}

	@Override
	protected void drawToolTip(ItemDisplayDelegate item, ScreenRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		renderContext.drawToolTip(item.displayStack(), mouseX, mouseY);
	}
}
