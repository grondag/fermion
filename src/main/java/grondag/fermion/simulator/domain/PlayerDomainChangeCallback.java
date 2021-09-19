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
package grondag.fermion.simulator.domain;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

/**
 * Posted when player active domain changes.
 */
public interface PlayerDomainChangeCallback {
	Event<PlayerDomainChangeCallback> EVENT = EventFactory.createArrayBacked(PlayerDomainChangeCallback.class, (listeners) -> (p, o, n) -> {
		for (final PlayerDomainChangeCallback event : listeners) {
			event.onDomainChange(p, o, n);
		}
	});

	void onDomainChange(Player player, IDomain oldDomain, IDomain newDomain);
}
