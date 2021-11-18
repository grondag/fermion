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

package grondag.fermion.bits;

import java.lang.reflect.Array;

/**
 * Used for fast mapping of a enum to boolean values serialized to a numeric
 * primitive.
 */
@Deprecated
public class EnumBitSet<T extends Enum<?>> {
	private final T[] values;

	private final Class<T> clazz;

	public EnumBitSet(Class<T> clazz) {
		this.clazz = clazz;
		this.values = clazz.getEnumConstants();
	}

	/**
	 * Number of distinct values for flag values produced and consumed by this
	 * instance. Derivation is trivially simple. Main use is for clarity.
	 */
	public final int combinationCount() {
		return 2 << (values.length - 1);
	}

	public final int getFlagsForIncludedValues(@SuppressWarnings("unchecked") T... included) {
		int result = 0;
		for (final T e : included) {
			result |= (1 << e.ordinal());
		}
		return result;
	}

	public int getFlagsForIncludedValues(T v0, T v1, T v2, T v3) {
		return (1 << v0.ordinal()) | (1 << v1.ordinal()) | (1 << v2.ordinal()) | (1 << v3.ordinal());
	}

	public int getFlagsForIncludedValues(T v0, T v1, T v2) {
		return (1 << v0.ordinal()) | (1 << v1.ordinal()) | (1 << v2.ordinal());
	}

	public final int getFlagsForIncludedValues(T v0, T v1) {
		return (1 << v0.ordinal()) | (1 << v1.ordinal());
	}

	public final int getFlagForValue(T v0) {
		return (1 << v0.ordinal());
	}

	public final int setFlagForValue(T v, int flagsIn, boolean isSet) {
		if (isSet)
			return flagsIn | (1 << v.ordinal());
		else
			return flagsIn & ~(1 << v.ordinal());
	}

	public final boolean isFlagSetForValue(T v, int flagsIn) {
		return (flagsIn & (1 << v.ordinal())) != 0;
	}

	public final T[] getValuesForSetFlags(int flagsIn) {
		@SuppressWarnings("unchecked")
		final
		T[] result = (T[]) Array.newInstance(clazz, Integer.bitCount(flagsIn));

		final int bitCount = Integer.SIZE - Integer.numberOfLeadingZeros(flagsIn);
		int j = 0;
		for (int i = 0; i < bitCount; i++) {
			if ((flagsIn & (1 << i)) != 0) {
				result[j++] = values[i];
			}
		}
		return result;
	}
}
