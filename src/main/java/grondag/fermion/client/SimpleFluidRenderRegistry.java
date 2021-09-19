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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import grondag.fermion.Fermion;

class SimpleFluidRenderRegistry implements SimpleSynchronousResourceReloadListener {
	static final SimpleSynchronousResourceReloadListener LISTENER = new SimpleFluidRenderRegistry();
	private static final HashMap<Fluid, SimpleFluidRenderHandler> HANDLERS = new HashMap<>();

	static void register(Fluid stillFluid, Fluid flowingFluid, int color, String stillSprite, String flowingSprite) {
		final SimpleFluidRenderHandler handler = new SimpleFluidRenderHandler(color, stillSprite, flowingSprite);
		FluidRenderHandlerRegistry.INSTANCE.register(stillFluid, handler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowingFluid, handler);
		HANDLERS.put(stillFluid, handler);
	}

	private final ResourceLocation id = new ResourceLocation(Fermion.MOD_ID, "fluid_handler");
	private final ImmutableList<ResourceLocation> deps = ImmutableList.of(ResourceReloadListenerKeys.MODELS, ResourceReloadListenerKeys.TEXTURES);

	private SimpleFluidRenderRegistry() {
	}

	@Override
	public ResourceLocation getFabricId() {
		return id;
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return deps;
	}

	public void reload(ResourceManager resourceManager) {
		HANDLERS.values().forEach(h -> h.reload());
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		HANDLERS.values().forEach(h -> h.reload());
	}
}
