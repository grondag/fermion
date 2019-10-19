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

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SimpleModels implements ModelVariantProvider {
	public static ModelVariantProvider MODEL_VARIANT_PROVIER = new SimpleModels();

	private static final HashMap<Identifier, UnbakedModel> models = new HashMap<>();

	public static void register(Identifier id, UnbakedModel unbakedModel) {
		models.put(id, unbakedModel);
	}

	private SimpleModels() {

	}

	@Override
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) {
		return models.get(new Identifier(modelId.getNamespace(), modelId.getPath()));
	}

	/**
	 * Prevents pinholes or similar artifacts along texture seams by nudging all
	 * texture coordinates slightly towards the vertex centroid of the UV
	 * coordinates.
	 */
	public static void contractUVs(int spriteIndex, Sprite sprite, MutableQuadView poly) {
		final float uPixels = (float) sprite.getWidth() / (sprite.getMaxU() - sprite.getMinU());
		final float vPixels = (float) sprite.getHeight() / (sprite.getMaxV() - sprite.getMinV());
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

		poly.sprite(0, spriteIndex, MathHelper.lerp(nudge, u0, uCenter), MathHelper.lerp(nudge, v0, vCenter));
		poly.sprite(1, spriteIndex, MathHelper.lerp(nudge, u1, uCenter), MathHelper.lerp(nudge, v1, vCenter));
		poly.sprite(2, spriteIndex, MathHelper.lerp(nudge, u2, uCenter), MathHelper.lerp(nudge, v2, vCenter));
		poly.sprite(3, spriteIndex, MathHelper.lerp(nudge, u3, uCenter), MathHelper.lerp(nudge, v3, vCenter));
	}
}
