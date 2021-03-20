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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import grondag.fermion.Fermion;
import grondag.fermion.simulator.persistence.AssignedNumber;
import grondag.fermion.simulator.persistence.DirtListener;
import grondag.fermion.simulator.persistence.DirtListenerProvider;
import grondag.fermion.simulator.persistence.Numbered;
import grondag.fermion.varia.NBTDictionary;
import grondag.fermion.varia.ReadWriteNBT;

public class Domain implements ReadWriteNBT, DirtListenerProvider, Numbered, IDomain {
	private static final String NBT_DOMAIN_SECURITY_ENABLED = NBTDictionary.GLOBAL.claim("domSecOn");
	private static final String NBT_DOMAIN_NAME = NBTDictionary.GLOBAL.claim("domName");
	private static final String NBT_DOMAIN_USERS = NBTDictionary.GLOBAL.claim("domUsers");

	private static final HashSet<Class<? extends IDomainCapability>> capabilityTypes = new HashSet<>();

	public static void registerCapability(Class<? extends IDomainCapability> capabilityType) {
		capabilityTypes.add(capabilityType);
	}

	private final DomainManager domainManager;
	int id;
	String name;
	boolean isSecurityEnabled;
	private final IdentityHashMap<Class<? extends IDomainCapability>, IDomainCapability> capabilities = new IdentityHashMap<>();

	private final EventBus eventBus = new EventBus();

	private final HashMap<String, DomainUser> users = new HashMap<>();

	// private constructor
	Domain(DomainManager domainManager) {
		this.domainManager = domainManager;

		capabilities.clear();
		if (!capabilityTypes.isEmpty()) {
			for (final Class<? extends IDomainCapability> capType : capabilityTypes) {
				try {
					IDomainCapability cap;
					cap = capType.newInstance();
					cap.setDomain(this);
					capabilities.put(capType, cap);
				} catch (final Exception e) {
					Fermion.LOG.error("Unable to create domain capability", e);
				}
			}
		}
	}

	Domain(DomainManager domainManager, NbtCompound tag) {
		this(domainManager);
		writeTag(tag);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V extends IDomainCapability> V getCapability(Class<V> capability) {
		return (V) capabilities.get(capability);
	}

	@Override
	public EventBus eventBus() {
		return eventBus;
	}

	@Override
	public List<DomainUser> getAllUsers() {
		return ImmutableList.copyOf(users.values());
	}

	@Override
	@Nullable
	public DomainUser findPlayer(PlayerEntity player) {
		return findUser(player.getUuidAsString());
	}

	@Override
	@Nullable
	public DomainUser findUser(String userUUID) {
		return users.get(userUUID);
	}

	@Override
	public boolean hasPrivilege(PlayerEntity player, Privilege privilege) {
		final DomainUser user = findPlayer(player);
		return user == null ? false : user.hasPrivilege(privilege);
	}

	/**
	 * Will return existing user if already exists.
	 */
	@Override
	public synchronized DomainUser addPlayer(PlayerEntity player) {
		DomainUser result = findPlayer(player);
		if (result == null) {
			result = new DomainUser(this, player);
			users.put(result.userName, result);
			domainManager.isDirty = true;
		}
		return result;
	}

	@Override
	public int getRawNumber() {
		return id;
	}

	@Override
	public void setAssignedNumber(int id) {
		this.id = id;
	}

	@Override
	public String numberType() {
		return AssignedNumber.DOMAIN;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		domainManager.isDirty = true;
	}

	@Override
	public boolean isSecurityEnabled() {
		return isSecurityEnabled;
	}

	@Override
	public void setSecurityEnabled(boolean isSecurityEnabled) {
		this.isSecurityEnabled = isSecurityEnabled;
		domainManager.isDirty = true;
	}

	@Override
	public void markDirty() {
		domainManager.isDirty = true;
	}

	@Override
	public void readTag(NbtCompound tag) {
		serializeNumber(tag);
		tag.putBoolean(NBT_DOMAIN_SECURITY_ENABLED, isSecurityEnabled);
		tag.putString(NBT_DOMAIN_NAME, name);

		final NbtList nbtUsers = new NbtList();

		if (!users.isEmpty()) {
			for (final DomainUser user : users.values()) {
				nbtUsers.add(user.toTag());
			}
		}
		tag.put(NBT_DOMAIN_USERS, nbtUsers);
	}

	@Override
	public void writeTag(@Nullable NbtCompound tag) {
		deserializeNumber(tag);
		isSecurityEnabled = tag.getBoolean(NBT_DOMAIN_SECURITY_ENABLED);
		name = tag.getString(NBT_DOMAIN_NAME);

		final NbtList nbtUsers = tag.getList(NBT_DOMAIN_USERS, 10);
		if (nbtUsers != null && !nbtUsers.isEmpty()) {
			for (int i = 0; i < nbtUsers.size(); ++i) {
				final DomainUser user = new DomainUser(this, nbtUsers.getCompound(i));
				users.put(user.userName, user);
			}
		}
	}

	public DomainManager domainManager() {
		return domainManager;
	}

	@Override
	public DirtListener getDirtListener() {
		return domainManager;
	}

	@Override
	public void afterDeserialization() {
		capabilities.values().forEach(c -> c.afterDeserialization());
	}

	@Override
	public void unload() {
		capabilities.values().forEach(c -> c.unload());
	}

	@Override
	public void loadNew() {
		capabilities.values().forEach(c -> c.loadNew());
	}

}
