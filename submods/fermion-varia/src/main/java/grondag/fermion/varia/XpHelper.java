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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class XpHelper {
	/** Adds or removes XP without adjusting player score */
	public static void changeXpNoScore(PlayerEntity player, int delta) {
		player.experienceProgress += (float)delta / (float)player.getNextLevelExperience();
		player.totalExperience = MathHelper.clamp(player.totalExperience + delta, 0, Integer.MAX_VALUE);

		while(player.experienceProgress < 0.0F) {
			float p = player.experienceProgress * player.getNextLevelExperience();
			
			if (player.experienceLevel > 0) {
				player.addExperienceLevels(-1);
				player.experienceProgress = 1.0F + p / player.getNextLevelExperience();
			} else {
				player.addExperienceLevels(-1);
				player.experienceProgress = 0.0F;
			}
		}

		while(player.experienceProgress >= 1.0F) {
			player.experienceProgress = (player.experienceProgress - 1.0F) * (float)player.getNextLevelExperience();
			player.addExperienceLevels(1);
			player.experienceProgress /= (float)player.getNextLevelExperience();
		}
	}
}
