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

package grondag.fermion.varia;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Assigns an integer handle to object instances that can later be used to
 * retrieve the same instance. Useful when need to serialize an object (in
 * memory) to a primitive type.
 * <p>
 *
 * Pointers will always be positive, non-zero values.
 * <p>
 *
 * Safe for concurrent use.
 *
 * @deprecated Use version in special-circumstances.
 */
@Deprecated
public class IndexedInterner<T> {
	private final AtomicInteger nextHandle = new AtomicInteger(1);

	private volatile T[] instances;

	private final ConcurrentHashMap<T, Integer> map = new ConcurrentHashMap<>();

	public IndexedInterner(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		final T[] a = (T[]) Array.newInstance(clazz, 64);
		instances = a;
	}

	public int toHandle(T object) {
		return map.computeIfAbsent(object, o -> {
			final int index = nextHandle.getAndIncrement();
			synchronized (this) {
				if (instances.length < index) {
					instances = Arrays.copyOf(instances, instances.length * 2);
				}
				instances[index - 1] = o;
			}
			return index;
		});
	}

	public T fromHandle(int handle) {
		return handle >= 0 ? instances[handle - 1] : null;
	}
}
