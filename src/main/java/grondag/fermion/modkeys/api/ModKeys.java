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
import net.minecraft.world.entity.player.Player;

public interface ModKeys {
	/**
	 * @deprecated Use one of them primary/secondary/tertiary conditions instead, to allow for client-side configuration by player.
	 */
	@Deprecated
	static boolean isShiftPressed(Player player) {
		return ModKeysAccess.isShiftPressed(player);
	}

	/**
	 * @deprecated Use one of the primary/secondary/tertiary conditions instead, to allow for client-side configuration by player.
	 */
	@Deprecated
	static boolean isControlPressed(Player player) {
		return ModKeysAccess.isControlPressed(player);
	}

	/**
	 * @deprecated Use one of the primary/secondary/tertiary conditions instead, to allow for client-side configuration by player.
	 */
	@Deprecated
	static boolean isAltPressed(Player player) {
		return ModKeysAccess.isAltPressed(player);
	}

	/**
	 * @deprecated Use one of the primary/secondary/tertiary conditions instead, to allow for client-side configuration by player.
	 */
	@Deprecated
	static boolean isSuperPressed(Player player) {
		return ModKeysAccess.isSuperPressed(player);
	}

	static boolean isPrimaryPressed(Player player) {
		return ModKeysAccess.isPrimaryPressed(player);
	}

	static boolean isSecondaryPressed(Player player) {
		return ModKeysAccess.isSecondaryPressed(player);
	}

	static boolean isTertiaryPressed(Player player) {
		return ModKeysAccess.isTertiaryPressed(player);
	}
}
