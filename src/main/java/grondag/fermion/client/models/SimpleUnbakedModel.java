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

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

/** Can be used for multiple blocks - will return same baked model for each */
public class SimpleUnbakedModel implements UnbakedModel {
	final Function<Function<SpriteIdentifier, Sprite>, BakedModel> baker;
	final List<SpriteIdentifier> sprites;
	BakedModel baked = null;

	public SimpleUnbakedModel(Function<Function<SpriteIdentifier, Sprite>, BakedModel> baker, List<SpriteIdentifier> sprites) {
		this.baker = baker;
		this.sprites = sprites;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> function, Set<Pair<String, String>> errors) {
		return sprites;
	}

	@Override
	@Nullable
	public BakedModel bake(ModelLoader modelLoader, Function<SpriteIdentifier, Sprite> spriteLoader, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		BakedModel result = baked;
		if (result == null) {
			result = baker.apply(spriteLoader);
			baked = result;
		}
		return result;
	}
}
