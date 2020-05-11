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
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;

public class OpenSignEditScreen extends Screen {
	public final OpenSignBlockEntity sign;
	private final SignBlockEntityRenderer.SignModel model = new SignBlockEntityRenderer.SignModel();
	protected int ticksSinceOpened;
	protected int currentRow;
	protected SelectionManager selectionManager;

	public OpenSignEditScreen(OpenSignBlockEntity blockEntity) {
		super(new TranslatableText("sign.edit", new Object[0]));
		sign = blockEntity;
	}

	@Override
	protected void init() {
		client.keyboard.enableRepeatEvents(true);

		addButton(new ButtonWidget(width / 2 - 100, height / 4 + 120, 200, 20, I18n.translate("gui.done"), (widget) -> {
			finishEditing();
		}));

		sign.setEditable(false);
		selectionManager = new SelectionManager(client, () -> {
			return sign.getTextOnRow(currentRow).getString();
		}, (s) -> {
			sign.setTextOnRow(currentRow, new LiteralText(s));
		}, 90);
	}

	@Override
	public void removed() {
		client.keyboard.enableRepeatEvents(false);
		OpenSignUpdateC2S.updateSignC2S(sign.getPos(), sign.getTextOnRow(0), sign.getTextOnRow(1), sign.getTextOnRow(2), sign.getTextOnRow(3));
		sign.setEditable(true);
	}

	@Override
	public void tick() {
		++ticksSinceOpened;

		if (!sign.getType().supports(sign.getCachedState().getBlock())) {
			finishEditing();
		}
	}

	protected void finishEditing() {
		sign.markDirty();
		client.openScreen(null);
	}

	@Override
	public boolean charTyped(char typed, int unused) {
		selectionManager.insert(typed);
		return true;
	}

	@Override
	public void onClose() {
		finishEditing();
	}

	@Override
	public boolean keyPressed(int key, int x, int y) {
		if (key == 265) {
			currentRow = currentRow - 1 & 3;
			selectionManager.moveCaretToEnd();
			return true;
		} else if (key != 264 && key != 257 && key != 335) {
			return selectionManager.handleSpecialKey(key) ? true : super.keyPressed(key, x, y);
		} else {
			currentRow = currentRow + 1 & 3;
			selectionManager.moveCaretToEnd();
			return true;
		}
	}

	@Override
	public void render(int i, int j, float tickDelta) {
		this.renderBackground();
		drawCenteredString(textRenderer, title.asFormattedString(), width / 2, 40, 16777215);
		final MatrixStack matrixStack = new MatrixStack();
		matrixStack.push();
		matrixStack.translate(width / 2, 0.0D, 50.0D);
		matrixStack.scale(-93.75F, -93.75F, -93.75F);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		matrixStack.translate(0.0D, -1.3125D, 0.0D);
		final BlockState blockState = sign.getCachedState();
		final boolean isStanding = blockState.getBlock() instanceof OpenSignBlock;

		if (!isStanding) {
			matrixStack.translate(0.0D, -0.3125D, 0.0D);
		}

		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		final VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
		final SpriteIdentifier lv = sign.getModelTexture();
		final SignBlockEntityRenderer.SignModel var10002 = model;
		var10002.getClass();
		final VertexConsumer vertexConsumer = lv.getVertexConsumer(immediate, var10002::getLayer);

		model.field.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
		if (isStanding) {

			model.field.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
		}

		matrixStack.pop();
		matrixStack.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		final int l = sign.getTextColor().getSignColor();
		final String[] strings = new String[4];

		for(int m = 0; m < strings.length; ++m) {
			strings[m] = sign.getTextBeingEditedOnRow(m, (text) -> {
				final List<Text> list = Texts.wrapLines(text, 90, client.textRenderer, false, true);
				return list.isEmpty() ? "" : list.get(0).asFormattedString();
			});
		}

		final Matrix4f matrix4f = matrixStack.peek().getModel();
		final int n = selectionManager.getSelectionStart();
		final int o = selectionManager.getSelectionEnd();
		final int p = client.textRenderer.isRightToLeft() ? -1 : 1;
		final int q = currentRow * 10 - sign.text.length * 5;

		final boolean fade = ticksSinceOpened / 6 % 2 == 0;


		for(int v = 0; v < strings.length; ++v) {
			final String line = strings[v];
			if (line != null) {
				final float s = -client.textRenderer.getStringWidth(line) / 2;
				client.textRenderer.draw(line, s, v * 10 - sign.text.length * 5, l, false, matrix4f, immediate, false, 0, 15728880);

				if (v == currentRow && n >= 0 && fade) {
					final int x = client.textRenderer.getStringWidth(line.substring(0, Math.max(Math.min(n, line.length()), 0)));
					final int y = (x - client.textRenderer.getStringWidth(line) / 2) * p;

					if (n >= line.length()) {
						client.textRenderer.draw("_", y, q, l, false, matrix4f, immediate, false, 0, 15728880);
					}
				}
			}
		}

		immediate.draw();

		for(int v = 0; v < strings.length; ++v) {
			final String line = strings[v];
			if (line != null && v == currentRow && n >= 0) {
				final int w = client.textRenderer.getStringWidth(line.substring(0, Math.max(Math.min(n, line.length()), 0)));
				final int x = (w - client.textRenderer.getStringWidth(line) / 2) * p;

				if (fade && n < line.length()) {
					final int var34 = q - 1;
					final int var10003 = x + 1;
					client.textRenderer.getClass();
					fill(matrix4f, x, var34, var10003, q + 9, -16777216 | l);
				}

				if (o != n) {
					final int y = Math.min(n, o);
					final int z = Math.max(n, o);
					final int aa = (client.textRenderer.getStringWidth(line.substring(0, y)) - client.textRenderer.getStringWidth(line) / 2) * p;
					final int ab = (client.textRenderer.getStringWidth(line.substring(0, z)) - client.textRenderer.getStringWidth(line) / 2) * p;
					final int ac = Math.min(aa, ab);
					final int ad = Math.max(aa, ab);
					final Tessellator tessellator = Tessellator.getInstance();
					final BufferBuilder bufferBuilder = tessellator.getBuffer();
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex(matrix4f, ac, q + 9, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, ad, q + 9, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, ad, q, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, ac, q, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.end();
					BufferRenderer.draw(bufferBuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrixStack.pop();
		super.render(i, j, tickDelta);
	}
}
