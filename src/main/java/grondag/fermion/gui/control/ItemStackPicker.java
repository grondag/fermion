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
import java.util.function.Function;
import java.util.function.ToLongFunction;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.gui.GuiUtil;
import grondag.fermion.gui.ScreenRenderContext;
import grondag.fermion.gui.ScreenTheme;

@Environment(EnvType.CLIENT)
public class ItemStackPicker<T> extends TabBar<T> {
	protected final MouseHandler<T> itemClickHandler;

	// avoids creating a new instance each frame
	protected final Matrix4f fontMatrix = new Matrix4f();
	protected final Function<T, ItemStack> stackFunc;
	protected final ToLongFunction<T> countFunc;

	// scales the glyphs
	protected float fontDrawScale;

	public ItemStackPicker(ScreenRenderContext renderContext, List<T> items, MouseHandler<T> itemClickHandler, Function<T, ItemStack> stackFunc, ToLongFunction<T> countFunc) {
		super(renderContext, items);
		this.itemClickHandler = itemClickHandler;
		this.stackFunc = stackFunc;
		this.countFunc = countFunc;
		setItemsPerRow(9);
		setSelectionEnabled(false);
	}

	public static int idealWidth(ScreenTheme theme, int itemsPerRow) {
		return theme.itemSlotSpacing * itemsPerRow - theme.itemSpacing + theme.internalMargin + theme.tabWidth;
	}

	// TODO: better labels for higher numbers
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
	protected void drawItem(MatrixStack matrixStack, T item, MinecraftClient mc, ItemRenderer itemRenderer, double left, double top, float partialTicks,
			boolean isHighlighted) {
		final int x = (int) left;
		final int y = (int) top;

		final ItemStack itemStack = stackFunc.apply(item);

		setZOffset(200);
		itemRenderer.zOffset = 200.0F;

		GuiUtil.renderItemAndEffectIntoGui(mc, itemRenderer, itemStack, x, y, itemSize);
		// TODO: support for dragging

		drawQuantity(matrixStack, countFunc.applyAsLong(item), x, y);

		setZOffset(0);
		itemRenderer.zOffset = 0.0F;
	}

	protected void drawQuantity(MatrixStack matrixStack, long qty, int left, int top) {
		if (qty < 2) {
			return;
		}

		final TextRenderer fontRenderer = renderContext.fontRenderer();
		final String qtyLabel = getQuantityLabel(qty);

		fontMatrix.loadIdentity();
		fontMatrix.multiply(Matrix4f.scale(fontDrawScale, fontDrawScale, 1));
		fontMatrix.multiply(Matrix4f.translate(0.0f, 0.0f, 200.0f));

		final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		final float x = (left + 8 - fontRenderer.getWidth(qtyLabel) * 0.5f * fontDrawScale) / fontDrawScale;
		final float y = (top + 17.5f) / fontDrawScale;
		fontRenderer.draw(qtyLabel, x + 0.15f, y, theme.itemCaptionColor, false, fontMatrix, immediate, true, 0, 15728880);
		fontRenderer.draw(qtyLabel, x - 0.15f, y, theme.itemCaptionColor, false, fontMatrix, immediate, true, 0, 15728880);
		immediate.draw();
	}

	@Override
	protected void handleCoordinateUpdate() {
		fontDrawScale = 6f / renderContext.fontRenderer().fontHeight;
		super.handleCoordinateUpdate();
	}

	@Override
	protected void setupItemRendering() {
		RenderSystem.disableDepthTest();
		RenderSystem.enableRescaleNormal();
		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void tearDownItemRendering() {
		RenderSystem.disableDepthTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (itemClickHandler != null && currentMouseLocation == MouseLocation.ITEM) {
			itemClickHandler.handle(clickedMouseButton, resourceForClickHandler());
		} else {
			super.handleMouseClick(mouseX, mouseY, clickedMouseButton);
		}
	}

	private @Nullable T resourceForClickHandler() {
		return get(currentMouseIndex);
	}

	@Override
	protected void drawItemToolTip(MatrixStack matrixStack, T item, ScreenRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		renderContext.renderTooltip(matrixStack, stackFunc.apply(item), mouseX, mouseY);
	}
}
