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

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class XpHelper {
	/** Adds or removes XP without adjusting player score */
	public static void changeXpNoScore(Player player, int delta) {
		player.experienceProgress += (float)delta / (float)player.getXpNeededForNextLevel();
		player.totalExperience = Mth.clamp(player.totalExperience + delta, 0, Integer.MAX_VALUE);

		while(player.experienceProgress < 0.0F) {
			final float p = player.experienceProgress * player.getXpNeededForNextLevel();

			if (player.experienceLevel > 0) {
				player.giveExperienceLevels(-1);
				player.experienceProgress = 1.0F + p / player.getXpNeededForNextLevel();
			} else {
				player.giveExperienceLevels(-1);
				player.experienceProgress = 0.0F;
			}
		}

		while(player.experienceProgress >= 1.0F) {
			player.experienceProgress = (player.experienceProgress - 1.0F) * player.getXpNeededForNextLevel();
			player.giveExperienceLevels(1);
			player.experienceProgress /= player.getXpNeededForNextLevel();
		}
	}
}
