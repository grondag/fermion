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
import org.jetbrains.annotations.Nullable;
import grondag.fermion.orientation.api.FaceEdge;

@Internal
public abstract class FaceEdgeHelper {
	private FaceEdgeHelper() {
	}

	private static final FaceEdge[] VALUES = FaceEdge.values();

	public static final int COUNT = VALUES.length;

	public static final FaceEdge fromOrdinal(int ordinal) {
		return VALUES[ordinal];
	}

	// find the side for a given face orthogonal to a face
	private final static FaceEdge FACE_LOOKUP[][] = new FaceEdge[6][6];

	static {
		for (final Direction onFace : Direction.values()) {
			for (final Direction edgeFace : Direction.values()) {
				FaceEdge match = null;

				for (final FaceEdge side : FaceEdge.values()) {
					if (side.toWorld(onFace) == edgeFace) {
						match = side;
					}
				}
				FACE_LOOKUP[onFace.ordinal()][edgeFace.ordinal()] = match;
			}
		}
	}

	/**
	 * Determines if the given sideFace is TOP, BOTTOM, DEFAULT_LEFT or
	 * DEFAULT_RIGHT of onFace. If none (sideFace on same orthogonalAxis as onFace),
	 * return null;
	 */
	@Nullable
	public static FaceEdge fromWorld(Direction edgeFace, Direction onFace) {
		return FACE_LOOKUP[onFace.ordinal()][edgeFace.ordinal()];
	}

	public static void forEach(Consumer<FaceEdge> consumer) {
		for (final FaceEdge val : VALUES) {
			consumer.accept(val);
		}
	}
}
