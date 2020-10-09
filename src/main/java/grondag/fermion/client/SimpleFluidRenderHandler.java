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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;

class SimpleFluidRenderHandler implements FluidRenderHandler {
	private final int color;
	private final Identifier stillSpriteName;
	private final Identifier flowingSpriteName;
	private final Sprite[] sprites = new Sprite[2];

	SimpleFluidRenderHandler(int color, String stillSpriteName, String flowingSpriteName) {
		this.color = color;
		this.stillSpriteName = new Identifier(stillSpriteName);
		this.flowingSpriteName = new Identifier(flowingSpriteName);
	}

	@Override
	public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
		return color;
	}

	@Override
	public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
		return sprites;
	}

	public void reload() {
		final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		sprites[0] = atlas.apply(stillSpriteName);
		sprites[1] = atlas.apply(flowingSpriteName);
	}
}
