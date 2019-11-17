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

import grondag.fermion.spatial.HorizontalAlignment;
import grondag.fermion.spatial.Rotation;
import grondag.fermion.spatial.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

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
	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			final double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			final double j = top;
			top = bottom;
			bottom = j;
		}

		final float alpha = (color >> 24 & 255) / 255.0F;
		final float red = (color >> 16 & 255) / 255.0F;
		final float green = (color >> 8 & 255) / 255.0F;
		final float blue = (color & 255) / 255.0F;
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBufferBuilder();

		GlStateManager.disableTexture();
		GlStateManager.color4f(red, green, blue, alpha);
		vertexbuffer.begin(7, VertexFormats.POSITION);
		vertexbuffer.vertex(left, bottom, 0.0D).next();
		vertexbuffer.vertex(right, bottom, 0.0D).next();
		vertexbuffer.vertex(right, top, 0.0D).next();
		vertexbuffer.vertex(left, top, 0.0D).next();
		tessellator.draw();
		GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.enableTexture();
	}

	/**
	 * Draws a horizontal of the given pixelWidth between two points.
	 */
	public static void drawHorizontalLine(double startX, double endX, double y, double width, int color) {
		if (endX < startX) {
			final double x = startX;
			startX = endX;
			endX = x;
		}

		final double halfWidth = width / 2;

		drawRect(startX - halfWidth, y - halfWidth, endX + halfWidth, y + halfWidth, color);
	}

	/**
	 * Draws a vertical of the given pixelWidth between two points.
	 */
	public static void drawVerticalLine(double x, double startY, double endY, double width, int color) {
		if (endY < startY) {
			final double y = startY;
			startY = endY;
			endY = y;
		}

		final double halfWidth = width / 2;

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
	//  GlStateManager.enableBlend();
	//  GlStateManager.disableTexture2D();
	//  GlStateManager.disableDepth();
	//  GL11.glLineWidth(2.0f);
	//  GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	//  GlStateManager.color(f, f1, f2, f3);
	//  buffer.pos(x1, y1, 0.0D).endVertex();
	//  buffer.pos(x2, y2, 0.0D).endVertex();
	//  tessellator.draw();
	//  GlStateManager.enableTexture2D();
	//  GlStateManager.enableDepth();
	//  GlStateManager.disableBlend();
	//}

	public static void drawBoxRightBottom(double left, double top, double right, double bottom, double lineWidth, int color) {
		drawVerticalLine(left, top, bottom, lineWidth, color);
		drawVerticalLine(right, top, bottom, lineWidth, color);
		drawHorizontalLine(left, right, top, lineWidth, color);
		drawHorizontalLine(left, right, bottom, lineWidth, color);
	}

	public static void drawBoxWidthHeight(double left, double top, double width, double height, double lineWidth, int color) {
		drawBoxRightBottom(left, top, left + width, top + height, lineWidth, color);
	}

	public static void drawQuad(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, int color) {
		final float f3 = (color >> 24 & 255) / 255.0F;
		final float f = (color >> 16 & 255) / 255.0F;
		final float f1 = (color >> 8 & 255) / 255.0F;
		final float f2 = (color & 255) / 255.0F;
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBufferBuilder();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
			GlStateManager.DestFactor.ZERO);
		GlStateManager.color4f(f, f1, f2, f3);
		vertexbuffer.begin(7, VertexFormats.POSITION);
		vertexbuffer.vertex(x0, y0, 0.0D).end();
		vertexbuffer.vertex(x1, y1, 0.0D).end();
		vertexbuffer.vertex(x2, y2, 0.0D).end();
		vertexbuffer.vertex(x3, y3, 0.0D).end();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	/**
	 * Draws a rectangle using the provide texture sprite and color
	 */
	public static void drawTexturedRectWithColor(double xCoord, double yCoord, double zLevel, Sprite textureSprite, double widthIn, double heightIn, int color,
		Rotation rotation, boolean useAlpha) {
		drawTexturedRectWithColor(heightIn, heightIn, heightIn, textureSprite, heightIn, heightIn, color, 1, rotation, useAlpha);
	}

	private static double[][] rotatedUV(double minU, double minV, double maxU, double maxV, Rotation rotation) {
		final double[][] result = new double[2][4];

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
		int textureDivision, Rotation rotation, boolean useAlpha) {
		final float alpha = (color >> 24 & 255) / 255.0F;
		final float red = (color >> 16 & 255) / 255.0F;
		final float green = (color >> 8 & 255) / 255.0F;
		final float blue = (color & 255) / 255.0F;

		final double minU = textureSprite.getMinU();
		final double minV = textureSprite.getMinV();
		final double maxU = minU + (textureSprite.getMaxU() - minU) / textureDivision;
		final double maxV = minV + (textureSprite.getMaxV() - minV) / textureDivision;
		final double uv[][] = rotatedUV(minU, minV, maxU, maxV, rotation);

		final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder vertexbuffer = tessellator.getBufferBuilder();
		GlStateManager.enableTexture();

		if (useAlpha) {
			GlStateManager.enableAlphaTest(); // should already be, but make sure
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
				GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		} else {
			GlStateManager.disableBlend();
			GlStateManager.disableAlphaTest();
		}

		GlStateManager.color4f(1, 1, 1, 1);

		vertexbuffer.begin(7, VertexFormats.POSITION_UV_COLOR);
		vertexbuffer.vertex(xCoord + 0, yCoord + heightIn, zLevel).texture(uv[0][0], uv[1][0]).color(red, green, blue, alpha).end();
		vertexbuffer.vertex(xCoord + widthIn, yCoord + heightIn, zLevel).texture(uv[0][1], uv[1][1]).color(red, green, blue, alpha).end();
		vertexbuffer.vertex(xCoord + widthIn, yCoord + 0, zLevel).texture(uv[0][2], uv[1][2]).color(red, green, blue, alpha).end();
		vertexbuffer.vertex(xCoord + 0, yCoord + 0, zLevel).texture(uv[0][3], uv[1][3]).color(red, green, blue, alpha).end();
		tessellator.draw();

		if (useAlpha) {
			GlStateManager.disableBlend();
		} else {
			GlStateManager.enableAlphaTest();
		}

		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();

	}

	public static void playPressedSound() {
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public static boolean renderItemAndEffectIntoGui(ScreenRenderContext renderContext, ItemStack itm, double x, double y, double contentSize) {
		return renderItemAndEffectIntoGui(renderContext.minecraft(), renderContext.renderItem(), itm, x, y, contentSize);
	}

	/**
	 * Size is in pixels. Hat tip to McJty.
	 */
	public static boolean renderItemAndEffectIntoGui(MinecraftClient mc, ItemRenderer itemRender, ItemStack itm, double x, double y, double contentSize) {
		boolean rc = false;

		if (itm != null && itm.getItem() != null) {
			rc = true;

			//TODO: This is certaniously borkified now

			//RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translated(x, y, 0);
			GlStateManager.scalef(1 / 16f, 1 / 16f, 1 / 16f);
			GlStateManager.scaled(contentSize, contentSize, contentSize);

			GlStateManager.enableRescaleNormal();
			//GLX.setLightmapTextureCoords(GLX.lightmapTexUnit, 240.0F, 240.0F);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableDepthTest();

			itemRender.renderGuiItemIcon(itm, 0, 0);

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			//            RenderHelper.disableStandardItemLighting();
		}

		return rc;
	}

	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public static void drawAlignedStringNoShadow(TextRenderer fontRendererIn, String text, float x, float y, float width, float height, int color,
		HorizontalAlignment hAlign, VerticalAlignment vAlign) {

		switch (hAlign) {
		case RIGHT:
			x += width - fontRendererIn.getStringWidth(text);
			break;

		case CENTER:
			x += (width - fontRendererIn.getStringWidth(text)) / 2;
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
		fontRendererIn.draw(text, x, y, color);
	}

	public static void drawAlignedStringNoShadow(TextRenderer fontRendererIn, String text, double x, double y, double width, double height, int color,
		HorizontalAlignment hAlign, VerticalAlignment vAlign) {
		drawAlignedStringNoShadow(fontRendererIn, text, (float) x, (float) y, (float) width, (float) height, color, hAlign, vAlign);
	}

	/**
	 * Renders the specified text to the screen. Args : renderer, string, x, y,
	 * color
	 */
	public static void drawStringNoShadow(TextRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.draw(text, x, y, color);
	}

}
