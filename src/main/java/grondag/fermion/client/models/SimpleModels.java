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

package grondag.fermion.client.models;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleModels implements ModelVariantProvider {
	public static ModelVariantProvider MODEL_VARIANT_PROVIER = new SimpleModels();

	private static final HashMap<ResourceLocation, UnbakedModel> models = new HashMap<>();

	public static void register(ResourceLocation id, UnbakedModel unbakedModel) {
		models.put(id, unbakedModel);
	}

	private SimpleModels() {

	}

	@Override
	public UnbakedModel loadModelVariant(ModelResourceLocation modelId, ModelProviderContext context) {
		return models.get(new ResourceLocation(modelId.getNamespace(), modelId.getPath()));
	}

	/**
	 * Prevents pinholes or similar artifacts along texture seams by nudging all
	 * texture coordinates slightly towards the vertex centroid of the UV
	 * coordinates.
	 */
	public static void contractUVs(int spriteIndex, TextureAtlasSprite sprite, MutableQuadView poly) {
		final float uPixels = sprite.getWidth() / (sprite.getU1() - sprite.getU0());
		final float vPixels = sprite.getHeight() / (sprite.getV1() - sprite.getV0());
		final float nudge = 4.0f / Math.max(vPixels, uPixels);

		final float u0 = poly.spriteU(0, spriteIndex);
		final float u1 = poly.spriteU(1, spriteIndex);
		final float u2 = poly.spriteU(2, spriteIndex);
		final float u3 = poly.spriteU(3, spriteIndex);

		final float v0 = poly.spriteV(0, spriteIndex);
		final float v1 = poly.spriteV(1, spriteIndex);
		final float v2 = poly.spriteV(2, spriteIndex);
		final float v3 = poly.spriteV(3, spriteIndex);

		final float uCenter = (u0 + u1 + u2 + u3) * 0.25F;
		final float vCenter = (v0 + v1 + v2 + v3) * 0.25F;

		poly.sprite(0, spriteIndex, Mth.lerp(nudge, u0, uCenter), Mth.lerp(nudge, v0, vCenter));
		poly.sprite(1, spriteIndex, Mth.lerp(nudge, u1, uCenter), Mth.lerp(nudge, v1, vCenter));
		poly.sprite(2, spriteIndex, Mth.lerp(nudge, u2, uCenter), Mth.lerp(nudge, v2, vCenter));
		poly.sprite(3, spriteIndex, Mth.lerp(nudge, u3, uCenter), Mth.lerp(nudge, v3, vCenter));
	}

	public static void emitBakedModelToMesh (BlockState blockState, BakedModel model, QuadEmitter qe) {
		final Random random = new Random();

		for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
			final Direction cullFace = ModelHelper.faceFromIndex(i);
			random.setSeed(42);
			final List<BakedQuad> quads = model.getQuads(blockState, cullFace, random);

			if (quads.isEmpty()) {
				continue;
			}

			for (final BakedQuad q : quads) {
				qe.fromVanilla(q.getVertices(), 0, false);
				qe.cullFace(cullFace);
				qe.nominalFace(q.getDirection());
				qe.colorIndex(q.getTintIndex());
				qe.emit();
			}
		}
	}
}
