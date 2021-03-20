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

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;

import grondag.fermion.Fermion;
import grondag.fermion.simulator.Simulator;
import grondag.fermion.simulator.persistence.AssignedNumber;
import grondag.fermion.simulator.persistence.Numbered;
import grondag.fermion.simulator.persistence.NumberedIndex;
import grondag.fermion.simulator.persistence.SimulationTopNode;
import grondag.fermion.varia.NBTDictionary;

public class DomainManager extends SimulationTopNode {
	private static final String NBT_DOMAIN_MANAGER = NBTDictionary.GLOBAL.claim("domMgr");
	private static final String NBT_DOMAIN_MANAGER_DOMAINS = NBTDictionary.GLOBAL.claim("domMgrAll");
	private static final String NBT_DOMAIN_PLAYER_DOMAINS = NBTDictionary.GLOBAL.claim("domMgrPlayer");
	private static final String NBT_DOMAIN_ACTIVE_DOMAINS = NBTDictionary.GLOBAL.claim("domMgrActive");

	/**
	 * Set to null when Simulator creates singleton and when it shuts down to force
	 * retrieval of current instance.
	 */
	private static DomainManager instance;

	public static DomainManager instance() {
		return instance;
	}

	private boolean isDeserializationInProgress = false;

	boolean isDirty = false;

	private boolean isLoaded = false;

	private IDomain defaultDomain;

	/**
	 * Each player has a domain that is automatically created for them and which
	 * they always own. This will be their initially active domain.
	 */
	private final HashMap<String, IDomain> playerIntrinsicDomains = new HashMap<>();

	/**
	 * Each player has a currently active domain. This will initially be their
	 * intrinsic domain.
	 */
	private final HashMap<String, IDomain> playerActiveDomains = new HashMap<>();

	/**
	 * If isNew=true then won't wait for a deserialize to become loaded.
	 */
	public DomainManager() {
		super(NBT_DOMAIN_MANAGER);
		// force refresh of singleton reference
		instance = null;

	}

	/**
	 * Called at shutdown
	 */
	@Override
	public void unload() {
		playerActiveDomains.clear();
		playerIntrinsicDomains.clear();
		defaultDomain = null;
		isLoaded = false;
	}

	@Override
	public void afterCreated(Simulator sim) {
		instance = this;
	}

	@Override
	public void loadNew() {
		unload();
		isLoaded = true;
	}

	/**
	 * Domain for unmanaged objects.
	 */
	public IDomain defaultDomain() {
		checkLoaded();
		if (defaultDomain == null) {
			defaultDomain = domainFromId(1);
			if (defaultDomain == null) {
				defaultDomain = new Domain(this);
				defaultDomain.setSecurityEnabled(false);
				defaultDomain.setAssignedNumber(Numbered.DEFAULT_NUM);
				defaultDomain.setName("Public");

				Simulator.instance().assignedNumbersAuthority().register(defaultDomain);
			}
		}
		return defaultDomain;
	}

	public List<IDomain> getAllDomains() {
		checkLoaded();
		final ImmutableList.Builder<IDomain> builder = ImmutableList.builder();
		for (final Numbered domain : Simulator.instance().assignedNumbersAuthority().getIndex(AssignedNumber.DOMAIN).values()) {
			builder.add((Domain) domain);
		}
		return builder.build();
	}

	public IDomain getDomain(int id) {
		checkLoaded();
		return domainFromId(id);
	}

	public synchronized IDomain createDomain() {
		checkLoaded();
		final Domain result = new Domain(this);
		Simulator.instance().assignedNumbersAuthority().register(result);
		result.name = "Domain " + result.id;
		isDirty = true;
		return result;
	}

	/**
	 * Does NOT destroy any of the contained objects in the domain!
	 */
	public synchronized void removeDomain(IDomain domain) {
		checkLoaded();
		Simulator.instance().assignedNumbersAuthority().unregister(domain);
		isDirty = true;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public void setDirty(boolean isDirty) {
		makeDirty(isDirty);
	}

	public void readNbt(NbtCompound tag) {
		isDeserializationInProgress = true;

		unload();

		// need to do this before loading domains, otherwise they will cause complaints
		isLoaded = true;

		if (tag == null) {
			return;
		}

		final NbtList nbtDomains = tag.getList(NBT_DOMAIN_MANAGER_DOMAINS, 10);
		if (nbtDomains != null && !nbtDomains.isEmpty()) {
			for (int i = 0; i < nbtDomains.size(); ++i) {
				final Domain domain = new Domain(this, nbtDomains.getCompound(i));
				Simulator.instance().assignedNumbersAuthority().register(domain);
			}
		}

		final NbtCompound nbtPlayerDomains = tag.getCompound(NBT_DOMAIN_PLAYER_DOMAINS);
		if (nbtPlayerDomains != null && !nbtPlayerDomains.isEmpty()) {
			for (final String playerName : nbtPlayerDomains.getKeys()) {
				final IDomain d = domainFromId(nbtPlayerDomains.getInt(playerName));
				if (d != null) {
					playerIntrinsicDomains.put(playerName, d);
				}
			}
		}

		final NbtCompound nbtActiveDomains = tag.getCompound(NBT_DOMAIN_ACTIVE_DOMAINS);
		if (nbtActiveDomains != null && !nbtActiveDomains.isEmpty()) {
			for (final String playerName : nbtActiveDomains.getKeys()) {
				final IDomain d = domainFromId(nbtActiveDomains.getInt(playerName));
				if (d != null) {
					playerActiveDomains.put(playerName, d);
				}
			}
		}

		isDeserializationInProgress = false;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		final NbtList nbtDomains = new NbtList();

		final NumberedIndex domains = Simulator.instance().assignedNumbersAuthority().getIndex(AssignedNumber.DOMAIN);

		if (!domains.isEmpty()) {
			for (final Numbered domain : domains.values()) {
				nbtDomains.add(((Domain) domain).toTag());
			}
		}
		tag.put(NBT_DOMAIN_MANAGER_DOMAINS, nbtDomains);

		if (!playerIntrinsicDomains.isEmpty()) {
			final NbtCompound nbtPlayerDomains = new NbtCompound();
			for (final Entry<String, IDomain> entry : playerIntrinsicDomains.entrySet()) {
				nbtPlayerDomains.putInt(entry.getKey(), entry.getValue().getAssignedNumber());
			}
			tag.put(NBT_DOMAIN_PLAYER_DOMAINS, nbtPlayerDomains);
		}

		if (!playerActiveDomains.isEmpty()) {
			final NbtCompound nbtActiveDomains = new NbtCompound();
			for (final Entry<String, IDomain> entry : playerActiveDomains.entrySet()) {
				nbtActiveDomains.putInt(entry.getKey(), entry.getValue().getAssignedNumber());
			}
			tag.put(NBT_DOMAIN_ACTIVE_DOMAINS, nbtActiveDomains);
		}
		return tag;
	}

	private boolean checkLoaded() {
		if (!isLoaded) {
			Fermion.LOG.warn("Domain manager accessed before it was loaded.  This is a bug and probably means simulation state has been lost.");
		}
		return isLoaded;
	}

	/**
	 * The player's currently active domain. If player has never specified, will be
	 * the player's intrinsic domain.
	 */
	public IDomain getActiveDomain(ServerPlayerEntity player) {
		IDomain result = playerActiveDomains.get(player.getUuidAsString());
		if (result == null) {
			synchronized (playerActiveDomains) {
				result = playerActiveDomains.get(player.getUuidAsString());
				if (result == null) {
					result = getIntrinsicDomain(player);
					playerActiveDomains.put(player.getUuidAsString(), result);
				}
			}
		}
		return result;
	}

	/**
	 * Set the player's currently active domain.<br>
	 * Posts an event so that anything dependent on active domain can react.
	 */
	public void setActiveDomain(ServerPlayerEntity player, IDomain domain) {
		synchronized (playerActiveDomains) {
			final IDomain result = playerActiveDomains.put(player.getUuidAsString(), domain);
			if (result == null || result != domain) {
				PlayerDomainChangeCallback.EVENT.invoker().onDomainChange(player, result, domain);
			}
		}
	}

	/**
	 * The player's private, default domain. Created if does not already exist.
	 */
	public IDomain getIntrinsicDomain(ServerPlayerEntity player) {
		IDomain result = playerIntrinsicDomains.get(player.getUuidAsString());
		if (result == null) {
			synchronized (playerIntrinsicDomains) {
				result = playerIntrinsicDomains.get(player.getUuidAsString());
				if (result == null) {
					result = createDomain();
					result.setSecurityEnabled(true);
					result.setName(I18n.translate("misc.default_domain_template", player.getName()));
					final DomainUser user = result.addPlayer(player);
					user.setPrivileges(Privilege.ADMIN);
					playerIntrinsicDomains.put(player.getUuidAsString(), result);
				}
			}
		}
		return result;
	}

	public boolean isDeserializationInProgress() {
		return isDeserializationInProgress;
	}

	// convenience object lookup methods
	public static IDomain domainFromId(int id) {
		return (Domain) Simulator.instance().assignedNumbersAuthority().get(id, AssignedNumber.DOMAIN);
	}

	@Override
	public void afterDeserialization() {
		final NumberedIndex domains = Simulator.instance().assignedNumbersAuthority().getIndex(AssignedNumber.DOMAIN);

		if (!domains.isEmpty()) {
			for (final Numbered domain : domains.values()) {
				((Domain) domain).afterDeserialization();
			}
		}
	}

	@Override
	public void makeDirty() {
		// TODO Auto-generated method stub

	}
}
