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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import org.lwjgl.opengl.GL11;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import grondag.fermion.orientation.api.ClockwiseRotation;

@Environment(EnvType.CLIENT)
public class GuiUtil {
	public static final double GOLDEN_RATIO = 1.618033988;

	/**
	 * Same as vanilla routine but accepts double values. Does not alter blend state
	 * so if you need alpha rendering have to do that before calling. Doing it here
	 * causes problems because it doesn't know what to restore it to.
	 */
	public static void drawRect(Matrix4f matrix, float left, float top, float right, float bottom, int color) {
		if (left < right) {
			final float i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			final float j = top;
			top = bottom;
			bottom = j;
		}

		final float alpha = (color >> 24 & 255) / 255.0F;
		final float red = (color >> 16 & 255) / 255.0F;
		final float green = (color >> 8 & 255) / 255.0F;
		final float blue = (color & 255) / 255.0F;

		final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.disableTexture();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(red, green, blue, alpha).endVertex();
		bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(red, green, blue, alpha).endVertex();
		bufferBuilder.vertex(matrix, right, top, 0.0F).color(red, green, blue, alpha).endVertex();
		bufferBuilder.vertex(matrix, left, top, 0.0F).color(red, green, blue, alpha).endVertex();
		bufferBuilder.end();
		BufferUploader.end(bufferBuilder);
		RenderSystem.enableTexture();
	}

	public static void drawGradientRect(Matrix4f matrix, float left, float top, float right, float bottom, int color1, int color2) {
			if (left < right) {
				final float i = left;
				left = right;
				right = i;
			}

			if (top < bottom) {
				final float j = top;
				top = bottom;
				bottom = j;
			}

			final float alpha1 = ((color1 >> 24) & 255) / 255.0F;
			final float red1 = ((color1 >> 16) & 255) / 255.0F;
			final float green1 = ((color1 >> 8) & 255) / 255.0F;
			final float blue1 = (color1 & 255) / 255.0F;

			final float alpha2 = ((color2 >> 24) & 255) / 255.0F;
			final float red2 = ((color2 >> 16) & 255) / 255.0F;
			final float green2 = ((color2 >> 8) & 255) / 255.0F;
			final float blue2 = (color2 & 255) / 255.0F;

			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();

			final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);

			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.vertex(left, bottom, 0.0D).color(red1, green1, blue1, alpha1).endVertex();
			bufferBuilder.vertex(right, bottom, 0.0D).color(red1, green1, blue1, alpha1).endVertex();
			bufferBuilder.vertex(right, top, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
			bufferBuilder.vertex(left, top, 0.0D).color(red2, green2, blue2, alpha2).endVertex();
			bufferBuilder.end();

			BufferUploader.end(bufferBuilder);
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
	}

	/**
	 * Draws a horizontal of the given pixelWidth between two points.
	 */
	public static void drawHorizontalLine(Matrix4f matrix, float startX, float endX, float y, float width, int color) {
		if (endX < startX) {
			final float x = startX;
			startX = endX;
			endX = x;
		}

		final float halfWidth = width / 2;

		drawRect(matrix, startX - halfWidth, y - halfWidth, endX + halfWidth, y + halfWidth, color);
	}

	/**
	 * Draws a vertical of the given pixelWidth between two points.
	 */
	public static void drawVerticalLine(Matrix4f matrix, float x, float startY, float endY, float width, int color) {
		if (endY < startY) {
			final float y = startY;
			startY = endY;
			endY = y;
		}

		final float halfWidth = width / 2;

		drawRect(matrix, x - halfWidth, startY - halfWidth, x + halfWidth, endY + halfWidth, color);
	}

	public static void drawBoxRightBottom(Matrix4f matrix, float left, float top, float right, float bottom, float lineWidth, int color) {
		drawVerticalLine(matrix, left, top, bottom, lineWidth, color);
		drawVerticalLine(matrix, right, top, bottom, lineWidth, color);
		drawHorizontalLine(matrix, left, right, top, lineWidth, color);
		drawHorizontalLine(matrix, left, right, bottom, lineWidth, color);
	}

	public static void drawBoxWidthHeight(Matrix4f matrix, float left, float top, float width, float height, float lineWidth, int color) {
		drawBoxRightBottom(matrix, left, top, left + width, top + height, lineWidth, color);
	}

	public static void drawQuad(Matrix4f matrix, float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
			final float f3 = (color >> 24 & 255) / 255.0F;
			final float f = (color >> 16 & 255) / 255.0F;
			final float f1 = (color >> 8 & 255) / 255.0F;
			final float f2 = (color & 255) / 255.0F;

			final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
			RenderSystem.enableBlend();
			RenderSystem.disableTexture();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.vertex(x0, y0, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferBuilder.vertex(x1, y1, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferBuilder.vertex(x2, y2, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferBuilder.vertex(x3, y3, 0.0D).color(f, f1, f2, f3).endVertex();
			bufferBuilder.end();
			BufferUploader.end(bufferBuilder);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
	}

	/**
	 * Draws a rectangle using the provide texture sprite and color
	 */
	public static void drawTexturedRectWithColor(Matrix4f matrix, double xCoord, double yCoord, double zLevel, TextureAtlasSprite textureSprite, double widthIn, double heightIn, int color,
	ClockwiseRotation rotation, boolean useAlpha) {
		drawTexturedRectWithColor(matrix, heightIn, heightIn, heightIn, textureSprite, heightIn, heightIn, color, 1, rotation, useAlpha);
	}

	private static float[][] rotatedUV(float minU, float minV, float maxU, float maxV, ClockwiseRotation rotation) {
		final float[][] result = new float[2][4];

		int i;

		switch (rotation) {
			case ROTATE_NONE:
			default:
				i = 0;
				break;

			case ROTATE_90:
				i = 3;
				break;

			case ROTATE_180:
				i = 2;
				break;

			case ROTATE_270:
				i = 1;
				break;
		}

		result[0][i] = minU;
		result[1][i] = maxV;
		i = (i + 1) & 3;
		result[0][i] = maxU;
		result[1][i] = maxV;
		i = (i + 1) & 3;
		result[0][i] = maxU;
		result[1][i] = minV;
		i = (i + 1) & 3;
		result[0][i] = minU;
		result[1][i] = minV;

		return result;
	}

	public static void drawTexturedRectWithColor(Matrix4f matrix, double xCoord, double yCoord, double zLevel, TextureAtlasSprite textureSprite, double widthIn, double heightIn, int color,
		int textureDivision, ClockwiseRotation rotation, boolean useAlpha) {
		final float alpha = (color >> 24 & 255) / 255.0F;
		final float red = (color >> 16 & 255) / 255.0F;
		final float green = (color >> 8 & 255) / 255.0F;
		final float blue = (color & 255) / 255.0F;

		final float minU = textureSprite.getU0();
		final float minV = textureSprite.getV0();
		final float maxU = minU + (textureSprite.getU1() - minU) / textureDivision;
		final float maxV = minV + (textureSprite.getV1() - minV) / textureDivision;
		final float uv[][] = rotatedUV(minU, minV, maxU, maxV, rotation);

		final TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);
		textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		final BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
		RenderSystem.enableTexture();

		if (useAlpha) {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		} else {
			RenderSystem.disableBlend();
		}

		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferBuilder.vertex(xCoord + 0, yCoord + heightIn, zLevel).uv(uv[0][0], uv[1][0]).color(red, green, blue, alpha).endVertex();
		bufferBuilder.vertex(xCoord + widthIn, yCoord + heightIn, zLevel).uv(uv[0][1], uv[1][1]).color(red, green, blue, alpha).endVertex();
		bufferBuilder.vertex(xCoord + widthIn, yCoord + 0, zLevel).uv(uv[0][2], uv[1][2]).color(red, green, blue, alpha).endVertex();
		bufferBuilder.vertex(xCoord + 0, yCoord + 0, zLevel).uv(uv[0][3], uv[1][3]).color(red, green, blue, alpha).endVertex();
		bufferBuilder.end();
	    BufferUploader.end(bufferBuilder);

		if (useAlpha) {
			RenderSystem.disableBlend();
		}
	}

	public static void playPressedSound() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public static boolean renderItemAndEffectIntoGui(ScreenRenderContext renderContext, ItemStack itm, float x, float y, float contentSize) {
		return renderItemAndEffectIntoGui(renderContext.minecraft(), renderContext.renderItem(), itm, x, y, contentSize);
	}

	public static boolean renderItemAndEffectIntoGui(ScreenRenderContext renderContext, ItemStack itm, BakedModel model, float x, float y, float contentSize) {
		return renderItemAndEffectIntoGui(renderContext.minecraft(), renderContext.renderItem(), itm, model, x, y, contentSize);
	}

	public static boolean renderItemAndEffectIntoGui(Minecraft mc, ItemRenderer itemRender, ItemStack itemStack, float x, float y, float contentSize) {
		if (itemStack != null && itemStack.getItem() != null) {
			return renderItemAndEffectIntoGui(mc, itemRender, itemStack, itemRender.getModel(itemStack, null, null, 42), x, y, contentSize);
		}

		return false;
	}

	/**
	 * Size is in pixels. Hat tip to McJty.
	 */
	public static boolean renderItemAndEffectIntoGui(Minecraft mc, ItemRenderer itemRender, ItemStack itemStack, BakedModel model, float x, float y, float contentSize) {
		if (itemStack != null && itemStack.getItem() != null) {

			mc.getTextureManager().bindForSetup(TextureAtlas.LOCATION_BLOCKS);
			mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			final PoseStack matrixStack = RenderSystem.getModelViewStack();
			matrixStack.pushPose();
			matrixStack.translate(x, y, itemRender.blitOffset);

			final float half = contentSize * 0.5f;

			matrixStack.translate(half, half, contentSize * 2);
			matrixStack.scale(contentSize, -contentSize, contentSize);
			RenderSystem.applyModelViewMatrix();

			 final PoseStack dummyStack = new PoseStack();
			final MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
			final boolean frontLit = !model.usesBlockLight();

			if (frontLit) {
				Lighting.setupForFlatItems();
			}

			itemRender.render(itemStack, ItemTransforms.TransformType.GUI, false, dummyStack, immediate, 15728880, OverlayTexture.NO_OVERLAY, model);
			immediate.endBatch();

			if (frontLit) {
				Lighting.setupFor3DItems();
			}

			matrixStack.popPose();
			RenderSystem.applyModelViewMatrix();
			RenderSystem.disableBlend();

			if (itemStack.isDamaged()) {
				final float scale = contentSize / 16f;
				RenderSystem.disableTexture();
				final Tesselator tessellator = Tesselator.getInstance();
				final BufferBuilder bufferBuilder = tessellator.getBuilder();
				final float dmg = itemStack.getDamageValue();
				final float maxDmg = itemStack.getMaxDamage();
				final float ratio = Math.max(0.0F, (maxDmg - dmg) / maxDmg);
				final int width = Math.round(13.0F - dmg * 13.0F / maxDmg);
				final int color = Mth.hsvToRgb(ratio / 3.0F, 1.0F, 1.0F);
				bufferGuiQuad(matrixStack.last().pose(), bufferBuilder, x + 2 * scale, y + 13 * scale, 13 * scale, 2 * scale, 0, 0, 0, 255);
				bufferBuilder.end();
			    BufferUploader.end(bufferBuilder);
				bufferGuiQuad(matrixStack.last().pose(), bufferBuilder, x + 2 * scale, y + 13.5f * scale, width * scale, scale, color >> 16 & 255, color >> 8 & 255, color & 255, 255);
				bufferBuilder.end();
			    BufferUploader.end(bufferBuilder);

				RenderSystem.enableTexture();
			}

			return true;
		} else {
			return false;
		}
	}

	private static void bufferGuiQuad(Matrix4f matrix, BufferBuilder bufferBuilder, float left, float top, float width, float height, int r, int g, int b, int a) {
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		bufferBuilder.vertex(matrix, left, top, 0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(matrix, left, top + height, 0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(matrix, left + width, top + height, 0).color(r, g, b, a).endVertex();
		bufferBuilder.vertex(matrix, left + width, top, 0).color(r, g, b, a).endVertex();
	}

	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public static void drawAlignedStringNoShadow(PoseStack matrixStack, Font fontRendererIn, Component text, float x, float y, float width, float height, int color,
	HorizontalAlignment hAlign, VerticalAlignment vAlign) {

		switch (hAlign) {
			case RIGHT:
				x += width - fontRendererIn.width(text);
				break;

			case CENTER:
				x += (width - fontRendererIn.width(text)) / 2;
				break;

			case LEFT:
			default:
				break;

		}

		switch (vAlign) {
			case BOTTOM:
				y += height - fontRendererIn.lineHeight;
				break;

			case MIDDLE:
				y += (height - fontRendererIn.lineHeight) / 2;
				break;

			case TOP:
			default:
				break;

		}

		fontRendererIn.draw(matrixStack, text, x, y, color);
	}

	public static void drawAlignedStringNoShadow(PoseStack matrixStack, Font fontRendererIn, Component text, double x, double y, double width, double height, int color,
	HorizontalAlignment hAlign, VerticalAlignment vAlign) {
		drawAlignedStringNoShadow(matrixStack, fontRendererIn, text, (float) x, (float) y, (float) width, (float) height, color, hAlign, vAlign);
	}

	/**
	 * Renders the specified text to the screen. Args : renderer, string, x, y,
	 * color
	 */
	public static void drawStringNoShadow(PoseStack matrixStack, Font fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.draw(matrixStack, text, x, y, color);
	}
}
