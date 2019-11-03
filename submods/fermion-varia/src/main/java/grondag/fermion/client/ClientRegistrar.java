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

package grondag.fermion.client;

import java.util.List;

import com.google.common.collect.ImmutableList;

import grondag.fermion.client.models.SimpleModels;
import grondag.fermion.client.models.SimpleRandomModel;
import grondag.fermion.client.models.SimpleUnbakedModel;
import grondag.fermion.registrar.AbstractRegistrar;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class ClientRegistrar extends AbstractRegistrar {

	public ClientRegistrar(String modId) {
		super(modId);
	}

	public void fluidRenderHandler(Fluid stillFluid, Fluid flowingFluid, int color, String stillSprite, String flowingSprite) {
		SimpleFluidRenderRegistry.register(stillFluid, flowingFluid, color, stillSprite, flowingSprite);
	}

	public void modelVariant(String id, UnbakedModel unbakedModel) {
		SimpleModels.register(id(id), unbakedModel);
	}

	public void simpleRandomModel(String id, String... textures) {
		final ImmutableList.Builder<Identifier> builder = ImmutableList.builder();

		for (final String tex : textures) {
			builder.add(id(tex));
		}

		final List<Identifier> list = builder.build();

		SimpleModels.register(id(id), new SimpleUnbakedModel(spriteMap -> new SimpleRandomModel(spriteMap, list), list));
	}
}
