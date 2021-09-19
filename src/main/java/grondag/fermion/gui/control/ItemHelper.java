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

package grondag.fermion.gui.control;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemHelper {
	// for unit testing
	public static class TestItemStack {

		public Item item;
		public int size;
		public CompoundTag tag;
		public int meta;

		public TestItemStack(Item item, int size, int meta, CompoundTag tag) {
			this.item = item;
			this.size = size;
			this.meta = meta;
			this.tag = tag;
		}

		public Item getItem() {
			return item;
		}

		public boolean hasTagCompound() {
			return tag != null;
		}

		public CompoundTag getTagCompound() {
			return tag;
		}

		public int getMetadata() {
			return meta;
		}

		public boolean areCapsCompatible(TestItemStack stack1) {
			return true;
		}

	}

	public static CompoundTag getOrCreateStackTag(ItemStack stack) {
		CompoundTag result = stack.getTag();
		if (result == null) {
			result = new CompoundTag();
			stack.setTag(result);
		}
		return result;
	}

	/**
	 * True if item stacks can stack with each other - does not check for stack
	 * limit
	 */
	public static boolean canStacksCombine(ItemStack stack1, ItemStack stack2)
	// public static boolean canStacksCombine(ItemStack stack1, ItemStack stack2)
	{
		if (stack1.isEmpty())
			return false;
		else if (stack1.getItem() != stack2.getItem())
			return false;
		else if (stack1.hasTag() ^ stack2.hasTag())
			return false;
		else if (stack1.hasTag() && !stack1.getTag().equals(stack2.getTag()))
			return false;
		else if (stack1.getCount() != stack2.getCount())
			return false;

		return true;
	}

	/**
	 * Returns hash codes that should be equal if
	 * {@link #canStacksCombine(ItemStack, ItemStack)} returns true; Does not
	 * consider capabilities in hash code.
	 */
	//    public static int stackHashCode(ItemStack stack)
	public static int stackHashCode(TestItemStack stack) {
		final Item item = stack.getItem();

		if (item == null)
			return 0;

		int hash = item.hashCode();

		if (stack.hasTagCompound()) {
			hash = hash * 7919 + stack.getTagCompound().hashCode();
		}

		if (stack.getMetadata() != 0) {
			hash = hash * 7919 + stack.getMetadata();
		}

		return hash;
	}
}
