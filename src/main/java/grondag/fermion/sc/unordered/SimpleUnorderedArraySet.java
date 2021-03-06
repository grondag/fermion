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

package grondag.fermion.sc.unordered;

/**
 * Version of SimpleUnorderedArrayList that uses equals instead of = for search.
 * Does not support null values.
 */
public class SimpleUnorderedArraySet<T> extends AbstractUnorderedArrayList<T> {

	/**
	 * Will replace matching value if exists. Returns existing value that was
	 * replaced if checked, null otherwise.
	 */
	public T put(T newItem) {
		for (int i = size - 1; i >= 0; i--) {
			if (items[i].equals(newItem)) {
				@SuppressWarnings("unchecked")
				final
				T result = (T) items[i];
				items[i] = newItem;
				return result;
			}
		}
		super.add(newItem);
		return null;
	}

	/**
	 * This will not update existing values. Use {@link #put(Object)} if you want
	 * that behavior. Returns existing value if checked, null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public T putIfNotPresent(T newItem) {
		for (int i = size - 1; i >= 0; i--) {
			if (items[i].equals(newItem))
				return (T) items[i];
		}
		super.add(newItem);
		return null;
	}

	/**
	 * Returns item that equals the given object, if checked. Null if not checked.
	 */
	@SuppressWarnings("unchecked")
	public T get(T itemToFind) {
		for (int i = size - 1; i >= 0; i--) {
			if (items[i].equals(itemToFind))
				return (T) items[i];
		}

		return null;
	}

	@Override
	public int findIndex(T itemToFind) {
		for (int i = size - 1; i >= 0; i--) {
			if (items[i].equals(itemToFind))
				return i;
		}

		return -1;
	}

	/**
	 * Returns item that was removed if checked, null if nothing checked.
	 */
	public T removeIfPresent(T itemToRemove) {
		for (int i = size - 1; i >= 0; i--) {
			if (items[i].equals(itemToRemove)) {
				@SuppressWarnings("unchecked")
				final
				T result = (T) items[i];
				this.remove(i);
				return result;
			}
		}
		return null;
	}

	@Override
	public boolean contains(Object itemToFind) {
		for (int i = size - 1; i >= 0; i--) {
			if (items[i].equals(itemToFind))
				return true;
		}
		return false;
	}
}
