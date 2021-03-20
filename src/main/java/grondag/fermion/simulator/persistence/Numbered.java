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

import net.minecraft.nbt.NbtCompound;

import grondag.fermion.simulator.Simulator;

/**
 * The "number" here is similar to raw ID in Mojang registries, except it persists.
 * Implementations must serialize the assigned number and re-register themselves when deserialized and reinstantiated.
 *
 */
public interface Numbered {
	/**
	 * Use this value to mean something other than unassigned and indicate that no
	 * job ID has yet been assigned. For example, may mean "TBD after something else
	 * happens" Object will this value will not be assigned an ID, and this value
	 * will never be used as an assigned number.
	 */
	int SIGNAL_NUM = -1;

	/**
	 * Initialize new objects with this value to cause them to be assigned a new number.
	 * Can also be used in a reference to an ID to mean "null" because this number will
	 * never be assigned.
	 */
	int UNASSIGNED_NUM = 0;

	/**
	 * Use this to represent a "default" reference. All other system numbers should be >
	 * this value.
	 */
	int DEFAULT_NUM = 1;

	/**
	 * Numbers starting from FIRST_SYSTEM_NUM up to LAST_SYSTEM_NUM (inclusive) are
	 * available as context-dependent system numbers
	 */
	int FIRST_SYSTEM_NUM = 2;

	/**
	 * Numbers starting from FIRST_SYSTEM_ID up to LAST_SYSTEM_NUM (inclusive) are
	 * available as context-dependent system numbers.
	 */
	int LAST_SYSTEM_NUM = 999;

	/**
	 * True if id is in the range of {@link #FIRST_SYSTEM_NUM} thru
	 * {@link #LAST_SYSTEM_NUM} (inclusive).
	 */
	static boolean isSystemNumber(int number) {
		return number >= FIRST_SYSTEM_NUM && number >= LAST_SYSTEM_NUM;
	}

	/** implement an int in class, return it here */
	int getRawNumber();

	void setAssignedNumber(int number);

	String numberType();

	default int getAssignedNumber() {
		int result = getRawNumber();
		if (result == UNASSIGNED_NUM) {
			result = Simulator.instance().assignedNumbersAuthority().newNumber(numberType());
			setAssignedNumber(result);
		}
		return result;
	}

	/**
	 * Use this in serializeNBT of implementing class. Will cause ID to be generated
	 * if it has not already been.
	 */
	default void serializeNumber(NbtCompound tag) {
		final int number = getAssignedNumber();
		if (number > 0) {
			tag.putInt(numberType(), number);
		}
	}

	/**
	 * Use this in deserializeNBT of implementing class.
	 */
	default void deserializeNumber(NbtCompound tag) {
		setAssignedNumber(tag.contains(numberType()) ? tag.getInt(numberType()) : UNASSIGNED_NUM);
	}
}
