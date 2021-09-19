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

import org.jetbrains.annotations.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
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
	protected void drawItem(PoseStack matrixStack, T item, Minecraft mc, ItemRenderer itemRenderer, double left, double top, float partialTicks,
	boolean isHighlighted) {
		final int x = (int) left;
		final int y = (int) top;

		final ItemStack itemStack = stackFunc.apply(item);

		setBlitOffset(200);
		itemRenderer.blitOffset = 200.0F;

		GuiUtil.renderItemAndEffectIntoGui(mc, itemRenderer, itemStack, x, y, itemSize);
		// TODO: support for dragging

		drawQuantity(matrixStack, countFunc.applyAsLong(item), x, y);

		setBlitOffset(0);
		itemRenderer.blitOffset = 0.0F;
	}

	protected void drawQuantity(PoseStack matrixStack, long qty, int left, int top) {
		if (qty < 2) {
			return;
		}

		final Font fontRenderer = renderContext.fontRenderer();
		final String qtyLabel = getQuantityLabel(qty);

		fontMatrix.setIdentity();
		fontMatrix.multiply(Matrix4f.createScaleMatrix(fontDrawScale, fontDrawScale, 1));
		fontMatrix.multiply(Matrix4f.createTranslateMatrix(0.0f, 0.0f, 200.0f));

		final MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		final float x = (left + 8 - fontRenderer.width(qtyLabel) * 0.5f * fontDrawScale) / fontDrawScale;
		final float y = (top + 17.5f) / fontDrawScale;
		fontRenderer.drawInBatch(qtyLabel, x + 0.15f, y, theme.itemCaptionColor, false, fontMatrix, immediate, true, 0, 15728880);
		fontRenderer.drawInBatch(qtyLabel, x - 0.15f, y, theme.itemCaptionColor, false, fontMatrix, immediate, true, 0, 15728880);
		immediate.endBatch();
	}

	@Override
	protected void handleCoordinateUpdate() {
		fontDrawScale = 6f / renderContext.fontRenderer().lineHeight;
		super.handleCoordinateUpdate();
	}

	// FIX: remove or repair
	@Override
	protected void setupItemRendering() {
//		RenderSystem.disableDepthTest();
//		RenderSystem.enableRescaleNormal();
//		RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void tearDownItemRendering() {
//		RenderSystem.disableDepthTest();
//		RenderSystem.disableRescaleNormal();
//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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
	protected void drawItemToolTip(PoseStack matrixStack, T item, ScreenRenderContext renderContext, int mouseX, int mouseY, float partialTicks) {
		renderContext.renderTooltip(matrixStack, stackFunc.apply(item), mouseX, mouseY);
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}
}
