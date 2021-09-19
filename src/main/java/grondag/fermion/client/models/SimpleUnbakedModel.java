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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

/** Can be used for multiple blocks - will return same baked model for each */
public class SimpleUnbakedModel implements UnbakedModel {
	final Function<Function<Material, TextureAtlasSprite>, BakedModel> baker;
	final List<Material> sprites;
	BakedModel baked = null;

	public SimpleUnbakedModel(Function<Function<Material, TextureAtlasSprite>, BakedModel> baker, List<Material> sprites) {
		this.baker = baker;
		this.sprites = sprites;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> function, Set<Pair<String, String>> errors) {
		return sprites;
	}

	@Override
	@Nullable
	public BakedModel bake(ModelBakery modelLoader, Function<Material, TextureAtlasSprite> spriteLoader, ModelState modelBakeSettings, ResourceLocation identifier) {
		BakedModel result = baked;
		if (result == null) {
			result = baker.apply(spriteLoader);
			baked = result;
		}
		return result;
	}
}
