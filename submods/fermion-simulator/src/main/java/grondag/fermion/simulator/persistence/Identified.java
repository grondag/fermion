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
package grondag.fermion.simulator.persistence;

import grondag.fermion.simulator.Simulator;
import net.minecraft.nbt.CompoundTag;

/**
 * The ID here is similar to raw ID in Mojang registries, except it persists.
 * Implementations must serialize the ID and re-register themselves when deserialized and reinstantiated.
 *
 */
public interface Identified {
	/**
	 * Use this value to mean something other than unassigned and indicate that no
	 * job ID has yet been assigned. For example, may mean "TBD after something else
	 * happens" Object will this value will not be assigned an ID, and this value
	 * will never be used as an ID.
	 */
	int SIGNAL_ID = -1;

	/**
	 * Initialize new objects with this value to cause them to be assigned a new ID.
	 * Can also be used in a reference to an ID to mean "null" because this ID will
	 * never be assigned.
	 */
	int UNASSIGNED_ID = 0;

	/**
	 * Use this to represent a "default" reference. All other system IDs should be >
	 * this value.
	 */
	int DEFAULT_ID = 1;

	/**
	 * Numbers starting from FIRST_SYSTEM_ID up to LAST_SYSTEM_ID (inclusive) are
	 * available as context-dependent system identifiers
	 */
	int FIRST_SYSTEM_ID = 2;

	/**
	 * Numbers starting from FIRST_SYSTEM_ID up to LAST_SYSTEM_ID (inclusive) are
	 * available as context-dependent system identifiers
	 */
	int LAST_SYSTEM_ID = 999;

	/**
	 * True if id is in the range of {@link #FIRST_SYSTEM_ID} thru
	 * {@link #LAST_SYSTEM_ID} (inclusive).
	 */
	static boolean isSystemID(int id) {
		return id >= FIRST_SYSTEM_ID && id >= LAST_SYSTEM_ID;
	}

	/** implement an int in class, return it here */
	int getIdRaw();

	void setId(int id);

	AssignedNumber idType();

	default int getId() {
		int result = getIdRaw();
		if (result == UNASSIGNED_ID) {
			result = Simulator.instance().assignedNumbersAuthority().newNumber(idType());
			setId(result);
		}
		return result;
	}

	/**
	 * Use this in serializeNBT of implementing class. Will cause ID to be generated
	 * if it has not already been.
	 */
	default void serializeID(CompoundTag tag) {
		final int id = getId();
		if (id > 0) {
			tag.putInt(idType().tagName, id);
		}
	}

	/**
	 * Use this in deserializeNBT of implementing class.
	 */
	default void deserializeID(CompoundTag tag) {
		setId(tag.containsKey(idType().tagName) ? tag.getInt(idType().tagName) : UNASSIGNED_ID);
	}

}
