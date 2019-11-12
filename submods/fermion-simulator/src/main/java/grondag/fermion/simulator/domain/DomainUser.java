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

import java.util.HashSet;
import java.util.IdentityHashMap;

import javax.annotation.Nullable;

import grondag.fermion.Fermion;
import grondag.fermion.varia.NBTDictionary;
import grondag.fermion.varia.ReadWriteNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

public class DomainUser implements ReadWriteNBT, IDomainMember {
	private static final HashSet<Class<? extends IUserCapability>> capabilityTypes = new HashSet<>();

	public static void registerCapability(Class<? extends IUserCapability> capabilityType) {
		capabilityTypes.add(capabilityType);
	}

	private static final String DOMAIN_USER_NAME = NBTDictionary.claim("domUserName");
	private static final String DOMAIN_USER_UUID = NBTDictionary.claim("domUserUUID");
	private static final String DOMAIN_USER_FLAGS = NBTDictionary.claim("domUserFlags");

	private final IDomain domain;

	// TODO: encapsulate these
	public String userName;
	public String uuid;

	private int privilegeFlags;

	private final IdentityHashMap<Class<? extends IUserCapability>, IUserCapability> capabilities = new IdentityHashMap<>();

	public DomainUser(IDomain domain, PlayerEntity player) {
		this.domain = domain;
		userName = player.getEntityName();
		uuid = player.getUuidAsString();
		createCapabilities();
	}

	public DomainUser(IDomain domain, CompoundTag tag) {
		this.domain = domain;
		createCapabilities();
		writeTag(tag);
	}

	private void createCapabilities() {
		capabilities.clear();
		if (!capabilityTypes.isEmpty()) {
			for (final Class<? extends IUserCapability> capType : capabilityTypes) {
				try {
					IUserCapability cap;
					cap = capType.newInstance();
					cap.setDomainUser(this);
					capabilities.put(capType, cap);
				} catch (final Exception e) {
					Fermion.LOG.error("Unable to create domain user capability", e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <V extends IUserCapability> V getCapability(Class<V> capability) {
		return (V) capabilities.get(capability);
	}

	/**
	 * Will return true for admin users, regardless of other Privilege grants. Will
	 * also return true if security is disabled for the domain.
	 */
	public boolean hasPrivilege(Privilege p) {
		return !domain.isSecurityEnabled() || Privilege.PRIVILEGE_FLAG_SET.isFlagSetForValue(Privilege.ADMIN, privilegeFlags)
			|| Privilege.PRIVILEGE_FLAG_SET.isFlagSetForValue(p, privilegeFlags);
	}

	public void grantPrivilege(Privilege p, boolean hasPrivilege) {
		privilegeFlags = Privilege.PRIVILEGE_FLAG_SET.setFlagForValue(p, privilegeFlags, hasPrivilege);
		domain.makeDirty();

	}

	public void setPrivileges(Privilege... granted) {
		privilegeFlags = Privilege.PRIVILEGE_FLAG_SET.getFlagsForIncludedValues(granted);
		domain.makeDirty();

	}

	@Override
	public void readTag(CompoundTag nbt) {
		nbt.putString(DOMAIN_USER_NAME, userName);
		nbt.putString(DOMAIN_USER_UUID, uuid);
		nbt.putInt(DOMAIN_USER_FLAGS, privilegeFlags);

		if (!capabilities.isEmpty()) {
			for (final IUserCapability cap : capabilities.values()) {
				if (!cap.isSerializationDisabled()) {
					nbt.put(cap.tagName(), cap.toTag());
				}
			}
		}
	}

	@Override
	public void writeTag(@Nullable CompoundTag nbt) {
		userName = nbt.getString(DOMAIN_USER_NAME);
		uuid = nbt.getString(DOMAIN_USER_UUID);
		privilegeFlags = nbt.getInt(DOMAIN_USER_FLAGS);
		capabilities.clear();

		if (!capabilities.isEmpty()) {
			for (final IUserCapability cap : capabilities.values()) {
				if (nbt.containsKey(cap.tagName())) {
					cap.writeTag(nbt.getCompound(cap.tagName()));
				}
			}
		}
	}

	@Override
	public @Nullable IDomain getDomain() {
		return domain;
	}

}
