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

package grondag.fermion.modkeys;

import java.util.function.Function;

import io.github.prospector.modmenu.api.ModMenuApi;

import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModKeysModMenuHelper implements ModMenuApi {
	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return ModKeysConfigScreen::new;
	}

	@Override
	public String getModId() {
		return "fermion-modkeys";
	}
}
