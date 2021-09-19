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
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus.Internal;
import grondag.fermion.orientation.api.CubeRotation;

@Internal
public abstract class CubeRotationHelper {
	private CubeRotationHelper() {
	}

	private static final CubeRotation[] VALUES = CubeRotation.values();
	public static final int COUNT = VALUES.length;
	private static final CubeRotation[][] CORNER_LOOKUP = new CubeRotation[6][6];

	static {
		for (final CubeRotation edge : VALUES) {
			CORNER_LOOKUP[edge.bottom.ordinal()][edge.back.ordinal()] = edge;
		}
	}

	public static CubeRotation find(Direction face1, Direction face2) {
		return CORNER_LOOKUP[face1.ordinal()][face2.ordinal()];
	}

	public static final CubeRotation fromOrdinal(int ordinal) {
		return VALUES[ordinal];
	}

	public static void forEach(Consumer<CubeRotation> consumer) {
		for (final CubeRotation val : VALUES) {
			consumer.accept(val);
		}
	}
}
