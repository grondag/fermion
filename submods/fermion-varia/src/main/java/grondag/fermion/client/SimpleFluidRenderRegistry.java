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

import java.util.Collection;
import java.util.HashMap;

import com.google.common.collect.ImmutableList;

import grondag.fermion.Fermion;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.fluid.Fluid;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

class SimpleFluidRenderRegistry implements SimpleSynchronousResourceReloadListener {
	static final SimpleSynchronousResourceReloadListener LISTENER = new SimpleFluidRenderRegistry();
	private static final HashMap<Fluid, SimpleFluidRenderHandler> HANDLERS = new HashMap<>();

	static void register(Fluid stillFluid, Fluid flowingFluid, int color, String stillSprite, String flowingSprite) {
		final SimpleFluidRenderHandler handler = new SimpleFluidRenderHandler(color, stillSprite, flowingSprite);
		FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, handler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowingFluid, handler);
		HANDLERS.put(stillFluid, handler);
	}

	private final Identifier id = new Identifier(Fermion.MOD_ID, "fluid_handler");
	private final ImmutableList<Identifier> deps = ImmutableList.of(ResourceReloadListenerKeys.MODELS, ResourceReloadListenerKeys.TEXTURES);

	private SimpleFluidRenderRegistry() {
	}

	@Override
	public Identifier getFabricId() {
		return id;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return deps;
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		HANDLERS.values().forEach(h -> h.reload());
	}
}
