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

import java.util.function.Function;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;

class SimpleFluidRenderHandler implements FluidRenderHandler {
	private final int color;
	private final ResourceLocation stillSpriteName;
	private final ResourceLocation flowingSpriteName;
	private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[2];

	SimpleFluidRenderHandler(int color, String stillSpriteName, String flowingSpriteName) {
		this.color = color;
		this.stillSpriteName = new ResourceLocation(stillSpriteName);
		this.flowingSpriteName = new ResourceLocation(flowingSpriteName);
	}

	@Override
	public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
		return color;
	}

	@Override
	public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
		return sprites;
	}

	public void reload() {
		final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
		sprites[0] = atlas.apply(stillSpriteName);
		sprites[1] = atlas.apply(flowingSpriteName);
	}
}
