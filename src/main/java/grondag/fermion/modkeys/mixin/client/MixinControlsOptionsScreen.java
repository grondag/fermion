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

package grondag.fermion.modkeys.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import grondag.fermion.modkeys.ModKeysConfigScreen;

@Mixin(ControlsOptionsScreen.class)
public class MixinControlsOptionsScreen extends Screen {

	public MixinControlsOptionsScreen(Text title) {
		super(title);
	}

	@Inject(at = @At("RETURN"), method = "init()V")
	public void drawMenuButton(CallbackInfo info) {
		final Screen thisScreen =  this;

		addButton(new ButtonWidget(width - 100, 10, 90, 20, I18n.translate("config.modkeys.button"), (buttonWidget) -> {
			client.openScreen(new ModKeysConfigScreen(thisScreen));
		}));
	}
}
