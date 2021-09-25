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

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.InvalidateRenderStateCallback;

import grondag.fermion.client.models.SimpleModels;

public final class FermionClient implements ClientModInitializer  {
	@Override
	public void onInitializeClient() {
		final Minecraft client = Minecraft.getInstance();

		InvalidateRenderStateCallback.EVENT.register(() -> {
			RenderRefreshProxy.RENDER_REFRESH_HANDLER = p -> {
				client.levelRenderer.blockChanged(client.level, p, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), 8);
			};
		});

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> SimpleModels.MODEL_VARIANT_PROVIER);

		// TODO: put back
		//		ClientSidePacketRegistry.INSTANCE.register(OpenSignUpdateS2C.S2C_ID, OpenSignUpdateS2C::handleS2C);
	}
}
