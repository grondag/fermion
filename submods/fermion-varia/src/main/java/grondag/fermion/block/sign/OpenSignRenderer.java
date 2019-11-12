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
package grondag.fermion.block.sign;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.SignBlockEntityModel;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** open and extensible implementation of vanilla signs */
@Environment(EnvType.CLIENT)
public class OpenSignRenderer extends BlockEntityRenderer<OpenSignBlockEntity> {
	protected final Identifier texture;
	protected final SignBlockEntityModel model;

	public static boolean isScreen = false;

	public OpenSignRenderer(Identifier texture, SignBlockEntityModel model) {
		this.texture = texture;
		this.model = model;
	}

	@Override
	public void render(OpenSignBlockEntity be, double x, double y, double z, float tickDelta, int destroyStage) {
		final BlockState blockState = be.getCachedState();
		GlStateManager.pushMatrix();

		if (blockState.getBlock() instanceof OpenSignBlock) {
			GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			GlStateManager.rotatef(-(blockState.get(OpenSignBlock.ROTATION) * 360 / 16.0F), 0.0F, 1.0F, 0.0F);
			model.getSignpostModel().visible = true;
		} else {
			GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			GlStateManager.rotatef(-blockState.get(OpenWallSignBlock.FACING).asRotation(), 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
			model.getSignpostModel().visible = false;
		}

		if (destroyStage >= 0) {
			bindTexture(DESTROY_STAGE_TEXTURES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 2.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			bindTexture(getModelTexture(blockState.getBlock()));
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
		model.render();
		GlStateManager.popMatrix();
		final TextRenderer textRenderer = getFontRenderer();
		GlStateManager.translatef(0.0F, 0.33333334F, 0.046666667F);
		GlStateManager.scalef(0.010416667F, -0.010416667F, 0.010416667F);
		GlStateManager.normal3f(0.0F, 0.0F, -0.010416667F);
		GlStateManager.depthMask(false);
		final int color = be.getTextColor().getSignColor();

		if (destroyStage < 0) {
			if (!isScreen && be.isLit()) {
				disableLightmap(true);
				GlStateManager.disableLighting();
			}

			for(int i = 0; i < 4; ++i) {
				final String str = be.getTextBeingEditedOnRow(i, (text) -> {
					final List<Text> lines = TextComponentUtil.wrapLines(text, 90, textRenderer, false, true);
					return lines.isEmpty() ? "" : lines.get(0).asFormattedString();
				});

				if (str != null) {
					textRenderer.draw(str, -textRenderer.getStringWidth(str) / 2, i * 10 - be.text.length * 5, color);

					if (i == be.getCurrentRow() && be.getSelectionStart() >= 0) {
						final int caretWidth = textRenderer.getStringWidth(str.substring(0, Math.max(Math.min(be.getSelectionStart(), str.length()), 0)));
						final int offset = textRenderer.isRightToLeft() ? -1 : 1;
						final int left = (caretWidth - textRenderer.getStringWidth(str) / 2) * offset;
						final int selTop = i * 10 - be.text.length * 5;

						if (be.isCaretVisible()) {
							if (be.getSelectionStart() < str.length()) {
								DrawableHelper.fill(left, selTop - 1, left + 1, selTop + 9, 0xFF000000 | color);
							} else {
								textRenderer.draw("_", left, selTop, color);
							}
						}

						if (be.getSelectionEnd() != be.getSelectionStart()) {
							final int u0 = Math.min(be.getSelectionStart(), be.getSelectionEnd());
							final int u1 = Math.max(be.getSelectionStart(), be.getSelectionEnd());
							final int offset0 = (textRenderer.getStringWidth(str.substring(0, u0)) - textRenderer.getStringWidth(str) / 2) * offset;
							final int offset1 = (textRenderer.getStringWidth(str.substring(0, u1)) - textRenderer.getStringWidth(str) / 2) * offset;
							final int selLeft = Math.min(offset0, offset1);
							final int selRight = Math.max(offset0, offset1);
							drawSelection(selLeft, selTop, selRight, selTop + 9);
						}
					}
				}
			}

			if (!isScreen && be.isLit()) {
				GlStateManager.enableLighting();
				disableLightmap(false);
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
		if (destroyStage >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}

	}

	private Identifier getModelTexture(Block block_1) {
		return texture;
	}

	private void drawSelection(int left, int top, int right, int bottom) {
		final Tessellator tess = Tessellator.getInstance();
		final BufferBuilder buff = tess.getBufferBuilder();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		buff.begin(7, VertexFormats.POSITION);
		buff.vertex(left, bottom, 0.0D).next();
		buff.vertex(right, bottom, 0.0D).next();
		buff.vertex(right, top, 0.0D).next();
		buff.vertex(left, top, 0.0D).next();
		tess.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}
}
