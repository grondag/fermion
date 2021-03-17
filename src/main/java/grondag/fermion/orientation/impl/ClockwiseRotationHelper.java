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
package grondag.fermion.orientation.impl;

import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;

import grondag.fermion.orientation.api.ClockwiseRotation;

@Internal
public abstract class ClockwiseRotationHelper {
	private ClockwiseRotationHelper() {
	}

	private static final ClockwiseRotation[] VALUES = ClockwiseRotation.values();
	public static final int COUNT = VALUES.length;

	public static final ClockwiseRotation fromOrdinal(int ordinal) {
		return VALUES[ordinal];
	}

	public static void forEach(Consumer<ClockwiseRotation> consumer) {
		for (final ClockwiseRotation val : VALUES) {
			consumer.accept(val);
		}
	}
}
