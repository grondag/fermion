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

import net.minecraft.block.BlockState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** open and extensible implementation of vanilla signs */
@Environment(EnvType.CLIENT)
public class OpenSignRenderer extends BlockEntityRenderer<OpenSignBlockEntity> {
	private final SignBlockEntityRenderer.SignModel model = new SignBlockEntityRenderer.SignModel();

	public static boolean isScreen = false;

	public OpenSignRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	@Override
	public void render(OpenSignBlockEntity be, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		final BlockState blockState = be.getCachedState();
		matrixStack.push();
		float h;

		if (blockState.getBlock() instanceof OpenSignBlock) {
			matrixStack.translate(0.5D, 0.5D, 0.5D);
			h = -(blockState.get(OpenSignBlock.ROTATION) * 360 / 16.0F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			model.foot.visible = true;
		} else {
			matrixStack.translate(0.5D, 0.5D, 0.5D);
			h = -blockState.get(OpenWallSignBlock.FACING).asRotation();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			matrixStack.translate(0.0D, -0.3125D, -0.4375D);
			model.foot.visible = false;
		}

		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		final SpriteIdentifier lv = SignBlockEntityRenderer.getModelTexture(blockState.getBlock());
		final SignBlockEntityRenderer.SignModel var10002 = model;
		var10002.getClass();
		final VertexConsumer vertexConsumer = lv.getVertexConsumer(vertexConsumerProvider, var10002::getLayer);
		model.field.render(matrixStack, vertexConsumer, i, j);
		model.foot.render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
		final TextRenderer textRenderer = dispatcher.getTextRenderer();
		matrixStack.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		final int m = be.getTextColor().getSignColor();

		for(int n = 0; n < 4; ++n) {
			final String string = be.getTextBeingEditedOnRow(n, (text) -> {
				final List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
				return list.isEmpty() ? "" : list.get(0).asFormattedString();
			});
			if (string != null) {
				final float o = -textRenderer.getStringWidth(string) / 2;
				textRenderer.draw(string, o, n * 10 - be.text.length * 5, m, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, 0, i);
			}
		}

		matrixStack.pop();
	}

	//	public static class_4730 getModelTexture(Block block) {
	//		class_4719 lv2;
	//		if (block instanceof AbstractOpenSignBlock) {
	//			lv2 = ((AbstractOpenSignBlock)block).method_24025();
	//		} else {
	//			lv2 = class_4719.field_21676;
	//		}
	//
	//		return class_4722.method_24064(lv2);
	//	}
}
