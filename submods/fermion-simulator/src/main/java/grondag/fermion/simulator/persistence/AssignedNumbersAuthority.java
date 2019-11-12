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

import java.util.Arrays;

import javax.annotation.Nullable;

import grondag.fermion.Fermion;
import grondag.fermion.varia.NBTDictionary;
import grondag.fermion.varia.ReadWriteNBT;
import me.zeroeightsix.fiber.Identifier;
import net.minecraft.nbt.CompoundTag;

public class AssignedNumbersAuthority implements ReadWriteNBT, DirtNotifier {

	private static final String NBT_TAG = NBTDictionary.claim("assignedNumAuth");

	private DirtListener dirtKeeper = NullDirtListener.INSTANCE;

	public AssignedNumbersAuthority() {
		indexes = new IdentifiedIndex[AssignedNumber.values().length];
		for (int i = 0; i < AssignedNumber.values().length; i++) {
			indexes[i] = createIndex(AssignedNumber.values()[i]);
		}
		clear();
	}

	public IdentifiedIndex getIndex(Identifier numberType) {
		return new IdentifiedIndex(numberType);
	}

	public void register(Identified registrant) {
		indexes[registrant.idType().ordinal()].register(registrant);
	}

	public void unregister(Identified registrant) {
		indexes[registrant.idType().ordinal()].unregister(registrant);
	}

	@Nullable
	public Identified get(int id, AssignedNumber idType) {
		return indexes[idType.ordinal()].get(id);
	}

	public void clear() {
		lastID = new int[AssignedNumber.values().length];
		Arrays.fill(lastID, 999);
		for (int i = 0; i < AssignedNumber.values().length; i++) {
			indexes[i].clear();
		}
	}

	/**
	 * First ID returned for each type is 1000 to allow room for system IDs. System
	 * ID's should start at 1 to distinguish from missing/unset ID.
	 */
	public synchronized int newNumber(AssignedNumber numberType) {
		dirtKeeper.makeDirty();

		return ++lastID[numberType.ordinal()];
	}

	@Override
	public synchronized void writeTag(@Nullable CompoundTag tag) {
		final int input[] = tag.getIntArray(NBT_TAG);
		if (input.length == 0) {
			clear();
		} else {
			if (input.length == lastID.length) {
				lastID = Arrays.copyOf(input, input.length);
			} else {
				Fermion.LOG.warn("Simulation assigned numbers save data appears to be corrupt.  World may be borked.");
				clear();
				final int commonLength = Math.min(lastID.length, input.length);
				System.arraycopy(input, 0, lastID, 0, commonLength);
			}
		}
	}

	@Override
	public synchronized void readTag(CompoundTag tag) {
		tag.putIntArray(NBT_TAG, Arrays.copyOf(lastID, lastID.length));
	}

	@Override
	public void markDirty() {
		dirtKeeper.markDirty();
	}

	@Override
	public void setDirtKeeper(DirtKeeper keeper) {
		dirtKeeper = keeper;
	}

	public IdentifiedIndex getIndex(AssignedNumber idType) {
		return indexes[idType.ordinal()];
	}

}
