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

package grondag.fermion.varia;

import grondag.fermion.block.sign.OpenSignUpdateC2S;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

public class FermionVaria implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerSidePacketRegistry.INSTANCE.register(OpenSignUpdateC2S.C2S_ID, OpenSignUpdateC2S::handleC2S);
	}
}
