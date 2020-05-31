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

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.nbt.CompoundTag;

import grondag.fermion.varia.NBTDictionary;
import grondag.fermion.varia.ReadWriteNBT;

public class AssignedNumbersAuthority implements ReadWriteNBT, DirtNotifier {

	private static final String NBT_TAG = NBTDictionary.GLOBAL.claim("assignedNumAuth");

	private DirtListener dirtKeeper = NullDirtListener.INSTANCE;
	private final Object2ObjectOpenHashMap<String, NumberedIndex> indexes = new Object2ObjectOpenHashMap<>();

	public AssignedNumbersAuthority() {
		clear();
	}

	public NumberedIndex getIndex(String numberType) {
		return indexes.computeIfAbsent(numberType, nt -> new NumberedIndex(nt, this));
	}

	/**
	 * @deprecated Use method on index directly.
	 */
	@Deprecated
	public void register(Numbered registrant) {
		getIndex(registrant.numberType()).register(registrant);
	}

	/**
	 * @deprecated Use method on index directly.
	 */
	@Deprecated
	public void unregister(Numbered registrant) {
		getIndex(registrant.numberType()).unregister(registrant);
	}

	/**
	 * @deprecated Use method on index directly.
	 */
	@Deprecated
	@Nullable
	public Numbered get(int id, String numberType) {
		return getIndex(numberType).get(id);
	}

	public void clear() {
		indexes.values().forEach(i -> i.clear());
	}

	/**
	 * First ID returned for each type is 1000 to allow room for system IDs. System
	 * ID's should start at 1 to distinguish from missing/unset ID.
	 * @deprecated Use method on index directly.
	 */
	@Deprecated
	public synchronized int newNumber(String numberType) {
		dirtKeeper.makeDirty();
		return getIndex(numberType).newNumber();
	}

	@Override
	public synchronized void writeTag(CompoundTag tag) {
		final CompoundTag myTag = new CompoundTag();

		indexes.forEach((id, idx) -> {
			myTag.putInt(id, idx.lastId);
		});

		tag.put(NBT_TAG, myTag);
	}

	@Override
	public synchronized void readTag(CompoundTag tag) {
		clear();
		final CompoundTag myTag = tag.getCompound(NBT_TAG);

		if (myTag == null || myTag.isEmpty()) return;

		myTag.getKeys().forEach(k -> {
			getIndex(k).reset(myTag.getInt(k));
		});
	}

	@Override
	public void markDirty() {
		dirtKeeper.markDirty();
	}

	@Override
	public void setDirtKeeper(DirtKeeper keeper) {
		dirtKeeper = keeper;
	}
}
