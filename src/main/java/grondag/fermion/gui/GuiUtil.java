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
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.fermion.orientation.api.ClockwiseRotation;

@Environment(EnvType.CLIENT)
public class GuiUtil {

	public static final int MOUSE_LEFT = 0;
	public static final int MOUSE_MIDDLE = 2;
	public static final int MOUSE_RIGHT = 1;

	public static final double GOLDEN_RATIO = 1.618033988;

	/**
	 * Same as vanilla routine but accepts double values. Does not alter blend state
	 * so if you need alpha rendering have to do that before calling. Doing it here
	 * causes problems because it doesn't know what to restore it to.
	 */
	public static void drawRect(float left, float top, float right, float bottom, int color) {
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
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBuffer();

		RenderSystem.disableTexture();
		RenderSystem.color4f(red, green, blue, alpha);
		vertexbuffer.begin(7, VertexFormats.POSITION);
		vertexbuffer.vertex(left, bottom, 0.0D).next();
		vertexbuffer.vertex(right, bottom, 0.0D).next();
		vertexbuffer.vertex(right, top, 0.0D).next();
		vertexbuffer.vertex(left, top, 0.0D).next();
		tessellator.draw();
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.enableTexture();
	}

	public static void drawGradientRect(float left, float top, float right, float bottom, int color1, int color2) {
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
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(7425);

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBuffer();

		vertexbuffer.begin(7, VertexFormats.POSITION_COLOR);
		vertexbuffer.vertex(left, bottom, 0.0D).color(red1, green1, blue1, alpha1).next();
		vertexbuffer.vertex(right, bottom, 0.0D).color(red1, green1, blue1, alpha1).next();
		vertexbuffer.vertex(right, top, 0.0D).color(red2, green2, blue2, alpha2).next();
		vertexbuffer.vertex(left, top, 0.0D).color(red2, green2, blue2, alpha2).next();
		tessellator.draw();
		RenderSystem.color4f(1, 1, 1, 1);
		RenderSystem.shadeModel(7424);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}

	/**
	 * Draws a horizontal of the given pixelWidth between two points.
	 */
	public static void drawHorizontalLine(float startX, float endX, float y, float width, int color) {
		if (endX < startX) {
			final float x = startX;
			startX = endX;
			endX = x;
		}

		final float halfWidth = width / 2;

		drawRect(startX - halfWidth, y - halfWidth, endX + halfWidth, y + halfWidth, color);
	}

	/**
	 * Draws a vertical of the given pixelWidth between two points.
	 */
	public static void drawVerticalLine(float x, float startY, float endY, float width, int color) {
		if (endY < startY) {
			final float y = startY;
			startY = endY;
			endY = y;
		}

		final float halfWidth = width / 2;

		drawRect(x - halfWidth, startY - halfWidth, x + halfWidth, endY + halfWidth, color);
	}

	//  private static void drawLine(int x1, int y1, int x2, int y2, int color) {
	//  float f3 = (color >> 24 & 255) / 255.0F;
	//  float f = (color >> 16 & 255) / 255.0F;
	//  float f1 = (color >> 8 & 255) / 255.0F;
	//  float f2 = (color & 255) / 255.0F;
	//  Tessellator tessellator = Tessellator.getInstance();
	//  VertexBuffer buffer = tessellator.getBuffer();
	//
	//  buffer.begin(GL11.GL_LINES, VertexFormats.POSITION);
	//  RenderSystem.enableBlend();
	//  RenderSystem.disableTexture2D();
	//  RenderSystem.disableDepth();
	//  GL11.glLineWidth(2.0f);
	//  RenderSystem.tryBlendFuncSeparate(770, 771, 1, 0);
	//  RenderSystem.color(f, f1, f2, f3);
	//  buffer.pos(x1, y1, 0.0D).endVertex();
	//  buffer.pos(x2, y2, 0.0D).endVertex();
	//  tessellator.draw();
	//  RenderSystem.enableTexture2D();
	//  RenderSystem.enableDepth();
	//  RenderSystem.disableBlend();
	//}

	public static void drawBoxRightBottom(float left, float top, float right, float bottom, float lineWidth, int color) {
		drawVerticalLine(left, top, bottom, lineWidth, color);
		drawVerticalLine(right, top, bottom, lineWidth, color);
		drawHorizontalLine(left, right, top, lineWidth, color);
		drawHorizontalLine(left, right, bottom, lineWidth, color);
	}

	public static void drawBoxWidthHeight(float left, float top, float width, float height, float lineWidth, int color) {
		drawBoxRightBottom(left, top, left + width, top + height, lineWidth, color);
	}

	public static void drawQuad(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
		final float f3 = (color >> 24 & 255) / 255.0F;
		final float f = (color >> 16 & 255) / 255.0F;
		final float f1 = (color >> 8 & 255) / 255.0F;
		final float f2 = (color & 255) / 255.0F;
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		RenderSystem.color4f(f, f1, f2, f3);
		vertexbuffer.begin(7, VertexFormats.POSITION);
		vertexbuffer.vertex(x0, y0, 0.0D).next();
		vertexbuffer.vertex(x1, y1, 0.0D).next();
		vertexbuffer.vertex(x2, y2, 0.0D).next();
		vertexbuffer.vertex(x3, y3, 0.0D).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	/**
	 * Draws a rectangle using the provide texture sprite and color
	 */
	public static void drawTexturedRectWithColor(double xCoord, double yCoord, double zLevel, Sprite textureSprite, double widthIn, double heightIn, int color,
			ClockwiseRotation rotation, boolean useAlpha) {
		drawTexturedRectWithColor(heightIn, heightIn, heightIn, textureSprite, heightIn, heightIn, color, 1, rotation, useAlpha);
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

	public static void drawTexturedRectWithColor(double xCoord, double yCoord, double zLevel, Sprite textureSprite, double widthIn, double heightIn, int color,
			int textureDivision, ClockwiseRotation rotation, boolean useAlpha) {
		final float alpha = (color >> 24 & 255) / 255.0F;
		final float red = (color >> 16 & 255) / 255.0F;
		final float green = (color >> 8 & 255) / 255.0F;
		final float blue = (color & 255) / 255.0F;

		final float minU = textureSprite.getMinU();
		final float minV = textureSprite.getMinV();
		final float maxU = minU + (textureSprite.getMaxU() - minU) / textureDivision;
		final float maxV = minV + (textureSprite.getMaxV() - minV) / textureDivision;
		final float uv[][] = rotatedUV(minU, minV, maxU, maxV, rotation);

		final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBuffer();
		RenderSystem.enableTexture();

		if (useAlpha) {
			RenderSystem.enableAlphaTest(); // should already be, but make sure
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		} else {
			RenderSystem.disableBlend();
			RenderSystem.disableAlphaTest();
		}

		RenderSystem.color4f(1, 1, 1, 1);

		vertexbuffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
		vertexbuffer.vertex(xCoord + 0, yCoord + heightIn, zLevel).texture(uv[0][0], uv[1][0]).color(red, green, blue, alpha).next();
		vertexbuffer.vertex(xCoord + widthIn, yCoord + heightIn, zLevel).texture(uv[0][1], uv[1][1]).color(red, green, blue, alpha).next();
		vertexbuffer.vertex(xCoord + widthIn, yCoord + 0, zLevel).texture(uv[0][2], uv[1][2]).color(red, green, blue, alpha).next();
		vertexbuffer.vertex(xCoord + 0, yCoord + 0, zLevel).texture(uv[0][3], uv[1][3]).color(red, green, blue, alpha).next();
		tessellator.draw();

		if (useAlpha) {
			RenderSystem.disableBlend();
		} else {
			RenderSystem.enableAlphaTest();
		}

		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);

	}

	public static void playPressedSound() {
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public static boolean renderItemAndEffectIntoGui(ScreenRenderContext renderContext, ItemStack itm, float x, float y, float contentSize) {
		return renderItemAndEffectIntoGui(renderContext.minecraft(), renderContext.renderItem(), itm, x, y, contentSize);
	}

	/**
	 * Size is in pixels. Hat tip to McJty.
	 */
	public static boolean renderItemAndEffectIntoGui(MinecraftClient mc, ItemRenderer itemRender, ItemStack itemStack, float x, float y, float contentSize) {
		if (itemStack != null && itemStack.getItem() != null) {
			final BakedModel bakedModel = itemRender.getHeldItemModel(itemStack, null, null);

			RenderSystem.pushMatrix();
			mc.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			mc.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.translatef(x, y, itemRender.zOffset);

			final float half = contentSize * 0.5f;

			RenderSystem.translatef(half, half, 0.0F);
			RenderSystem.scalef(contentSize, -contentSize, contentSize);
			final MatrixStack matrixStack = new MatrixStack();
			final VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
			final Item item = itemStack.getItem();
			final boolean frontLit = !bakedModel.isSideLit() || item == Items.SHIELD || item == Items.TRIDENT;

			if (frontLit) {
				DiffuseLighting.disableGuiDepthLighting();
			}

			itemRender.renderItem(itemStack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
			immediate.draw();

			if (frontLit) {
				DiffuseLighting.enableGuiDepthLighting();
			}

			RenderSystem.disableRescaleNormal();
			RenderSystem.popMatrix();

			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();

			if (itemStack.isDamaged()) {
				final float scale = contentSize / 16f;
				RenderSystem.disableTexture();
				final Tessellator tessellator = Tessellator.getInstance();
				final BufferBuilder bufferBuilder = tessellator.getBuffer();
				final float dmg = itemStack.getDamage();
				final float maxDmg = itemStack.getMaxDamage();
				final float ratio = Math.max(0.0F, (maxDmg - dmg) / maxDmg);
				final int width = Math.round(13.0F - dmg * 13.0F / maxDmg);
				final int color = MathHelper.hsvToRgb(ratio / 3.0F, 1.0F, 1.0F);
				bufferGuiQuad(bufferBuilder, x + 2 * scale, y + 13 * scale, 13 * scale, 2 * scale, 0, 0, 0, 255);
				tessellator.draw();
				bufferGuiQuad(bufferBuilder, x + 2 * scale, y + 13.5f * scale, width * scale, scale, color >> 16 & 255, color >> 8 & 255, color & 255, 255);
				tessellator.draw();

				RenderSystem.enableTexture();
			}

			return true;
		} else {
			return false;
		}
	}

	private static void bufferGuiQuad(BufferBuilder bufferBuilder, float left, float top, float width, float height, int r, int g, int b, int a) {
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(left, top, 0).color(r, g, b, a).next();
		bufferBuilder.vertex(left, top + height, 0).color(r, g, b, a).next();
		bufferBuilder.vertex(left + width, top + height, 0).color(r, g, b, a).next();
		bufferBuilder.vertex(left + width, top, 0).color(r, g, b, a).next();
	}

	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public static void drawAlignedStringNoShadow(MatrixStack matrixStack, TextRenderer fontRendererIn, Text text, float x, float y, float width, float height, int color,
			HorizontalAlignment hAlign, VerticalAlignment vAlign) {

		switch (hAlign) {
		case RIGHT:
			x += width - fontRendererIn.getWidth(text);
			break;

		case CENTER:
			x += (width - fontRendererIn.getWidth(text)) / 2;
			break;

		case LEFT:
		default:
			break;

		}

		switch (vAlign) {
		case BOTTOM:
			y += height - fontRendererIn.fontHeight;
			break;

		case MIDDLE:
			y += (height - fontRendererIn.fontHeight) / 2;
			break;

		case TOP:
		default:
			break;

		}
		fontRendererIn.draw(matrixStack, text, x, y, color);
	}

	public static void drawAlignedStringNoShadow(MatrixStack matrixStack, TextRenderer fontRendererIn, String text, double x, double y, double width, double height, int color,
			HorizontalAlignment hAlign, VerticalAlignment vAlign) {
		drawAlignedStringNoShadow(matrixStack, fontRendererIn, text, (float) x, (float) y, (float) width, (float) height, color, hAlign, vAlign);
	}

	/**
	 * Renders the specified text to the screen. Args : renderer, string, x, y,
	 * color
	 */
	public static void drawStringNoShadow(MatrixStack matrixStack, TextRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.draw(matrixStack, text, x, y, color);
	}
}
