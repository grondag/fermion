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

package grondag.fermion.intstream;

public interface IntStream {
	int get(int address);

	void set(int address, int value);

	default void setFloat(int address, float value) {
		set(address, Float.floatToRawIntBits(value));
	}

	default float getFloat(int address) {
		return Float.intBitsToFloat(get(address));
	}

	default void copyFrom(int targetAddress, IntStream source, int sourceAddress, int length) {
		for (int i = 0; i < length; i++) {
			set(targetAddress + i, source.get(sourceAddress + i));
		}
	}

	default void copyFrom(int targetAddress, int[] source, int sourceIndex, int length) {
		for (int i = 0; i < length; i++) {
			set(targetAddress + i, source[sourceIndex + i]);
		}
	}

	default void copyTo(int sourceAddress, int[] target, int targetAddress, int length) {
		for (int i = 0; i < length; i++) {
			target[targetAddress + i] = get(sourceAddress + i);
		}
	}

	default void release() {
	}

	/**
	 * Sets all ints in the stream to zero. Does not deallocate any storage.
	 */
	void clear();

	/**
	 * Releases unused storage and partial blocks, if underlying implementation uses
	 * blocks. May cause unpooled allocation and later garbage collection, so use
	 * only when going to keep the stream around a while. Will become uncompacted if
	 * data are added.
	 */
	void compact();

	/**
	 * For testing purposes only, the actual number of ints allocated by the stream.
	 */
	int capacity();
}
