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
package grondag.fermion.modkeys.api;

import grondag.fermion.modkeys.impl.ModKeysAccess;
import net.minecraft.entity.player.PlayerEntity;

public interface ModKeys {
	static boolean isShiftPressed(PlayerEntity player) {
		return ModKeysAccess.isShiftPressed(player);
	}

	static boolean isControlPressed(PlayerEntity player) {
		return ModKeysAccess.isControlPressed(player);
	}

	static boolean isAltPressed(PlayerEntity player) {
		return ModKeysAccess.isAltPressed(player);
	}

	static boolean isSuperPressed(PlayerEntity player) {
		return ModKeysAccess.isSuperPressed(player);
	}
}
