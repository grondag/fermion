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
package grondag.fermion.orientation.api;

import static grondag.fermion.orientation.api.ClockwiseRotation.ROTATE_180;
import static grondag.fermion.orientation.api.ClockwiseRotation.ROTATE_270;
import static grondag.fermion.orientation.api.ClockwiseRotation.ROTATE_90;
import static grondag.fermion.orientation.api.ClockwiseRotation.ROTATE_NONE;

import java.util.Locale;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3i;

import grondag.fermion.orientation.impl.CubeEdgeHelper;

/**
 * Defines the twelve edges of a block and the relative position of neighboring
 * blocks diagonally adjacent to those edges. Use when shape is symmetrical with
 * respect to that edge.
 */
@Experimental
public enum CubeEdge implements StringIdentifiable {
	DOWN_SOUTH(Direction.DOWN, Direction.SOUTH, ROTATE_180),
	DOWN_WEST(Direction.DOWN, Direction.WEST, ROTATE_270),
	DOWN_NORTH(Direction.DOWN, Direction.NORTH, ROTATE_NONE),
	DOWN_EAST(Direction.DOWN, Direction.EAST, ROTATE_90),
	UP_NORTH(Direction.UP, Direction.NORTH, ROTATE_180),
	UP_EAST(Direction.UP, Direction.EAST, ROTATE_90),
	UP_SOUTH(Direction.UP, Direction.SOUTH, ROTATE_NONE),
	UP_WEST(Direction.UP, Direction.WEST, ROTATE_270),
	NORTH_EAST(Direction.NORTH, Direction.EAST, ROTATE_90),
	NORTH_WEST(Direction.NORTH, Direction.WEST, ROTATE_270),
	SOUTH_EAST(Direction.SOUTH, Direction.EAST, ROTATE_270),
	SOUTH_WEST(Direction.SOUTH, Direction.WEST, ROTATE_90);

	public final Direction face1;
	public final Direction face2;
	public final String name;

	/**
	 * Used to position models like stairs/wedges. Representation rotation around
	 * the parallel axis such that face1 and face2 are most occluded. Based on
	 * "default" model occluding north and down faces. Use the axis implied by
	 * face1.
	 */

	public final ClockwiseRotation rotation;

	public final Vec3i vector;

	/**
	 * Ordinal sequence that includes all faces, corner and far corners. Used to
	 * index them in a mixed array.
	 */
	@Internal
	public final int superOrdinal;

	@Internal
	public final int superOrdinalBit;

	/**
	 * Will be null if not a horizontal edge.
	 */
	@Nullable
	public final HorizontalEdge horizontalEdge;

	CubeEdge(Direction face1, Direction face2, ClockwiseRotation rotation) {
		name = name().toLowerCase(Locale.ROOT);
		this.face1 = face1;
		this.face2 = face2;
		this.rotation = rotation;
		superOrdinal = 6 + ordinal();
		superOrdinalBit = 1 << superOrdinal;

		final Vec3i v1 = face1.getVector();
		final Vec3i v2 = face2.getVector();
		vector = new Vec3i(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());

		if (face1.getAxis() == Axis.Y || face2.getAxis() == Axis.Y) {
			horizontalEdge = null;
		} else {
			horizontalEdge = HorizontalEdge.find(HorizontalFace.find(face1), HorizontalFace.find(face2));
		}
	}

	public static final int COUNT = CubeEdgeHelper.COUNT;

	/**
	 * Will be null if the inputs do not specify an edge.
	 */
	@Nullable
	public static CubeEdge find(Direction face1, Direction face2) {
		return CubeEdgeHelper.find(face1, face2);
	}

	public static final CubeEdge fromOrdinal(int ordinal) {
		return CubeEdgeHelper.fromOrdinal(ordinal);
	}

	public static void forEach(Consumer<CubeEdge> consumer) {
		CubeEdgeHelper.forEach(consumer);
	}

	@Override
	public String asString() {
		return name;
	}
}
