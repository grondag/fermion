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

import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import grondag.fermion.orientation.impl.CubeRotationHelper;

/**
 * Defines the twelve edges of a block and the relative position of neighboring
 * blocks diagonally adjacent to those edges when the orientation of those faces
 * matters - giving a total of twenty four total orientations.<p>
 *
 * This can also be used to represent oriented corners by keying off on
 * edge relative to the corner being modeled.<p>
 *
 * Components of the name are bottom and back face.
 */
@Experimental
public enum CubeRotation implements StringRepresentable {
	DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
	DOWN_WEST(Direction.DOWN, Direction.WEST),
	DOWN_NORTH(Direction.DOWN, Direction.NORTH),
	DOWN_EAST(Direction.DOWN, Direction.EAST),
	UP_NORTH(Direction.UP, Direction.NORTH),
	UP_EAST(Direction.UP, Direction.EAST),
	UP_SOUTH(Direction.UP, Direction.SOUTH),
	UP_WEST(Direction.UP, Direction.WEST),
	NORTH_EAST(Direction.NORTH, Direction.EAST),
	NORTH_WEST(Direction.NORTH, Direction.WEST),
	SOUTH_EAST(Direction.SOUTH, Direction.EAST),
	SOUTH_WEST(Direction.SOUTH, Direction.WEST),
	SOUTH_DOWN(Direction.SOUTH, Direction.DOWN),
	WEST_DOWN(Direction.WEST, Direction.DOWN),
	NORTH_DOWN(Direction.NORTH, Direction.DOWN),
	EAST_DOWN(Direction.EAST, Direction.DOWN),
	NORTH_UP(Direction.NORTH, Direction.UP),
	EAST_UP(Direction.EAST, Direction.UP),
	SOUTH_UP(Direction.SOUTH, Direction.UP),
	WEST_UP(Direction.WEST, Direction.UP),
	EAST_NORTH(Direction.EAST, Direction.NORTH),
	WEST_NORTH(Direction.WEST, Direction.NORTH),
	EAST_SOUTH(Direction.EAST, Direction.SOUTH),
	WEST_SOUTH(Direction.WEST, Direction.SOUTH);

	public final Direction bottom;
	public final Direction back;
	public final String name;

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

	CubeRotation(Direction bottom, Direction back) {
		name = name().toLowerCase(Locale.ROOT);
		this.bottom = bottom;
		this.back = back;
		superOrdinal = 6 + ordinal();
		superOrdinalBit = 1 << superOrdinal;

		final Vec3i v1 = bottom.getNormal();
		final Vec3i v2 = back.getNormal();
		vector = new Vec3i(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());

		if (bottom.getAxis() == Axis.Y || back.getAxis() == Axis.Y) {
			horizontalEdge = null;
		} else {
			horizontalEdge = HorizontalEdge.find(HorizontalFace.find(bottom), HorizontalFace.find(back));
		}
	}

	public static final int COUNT = CubeRotationHelper.COUNT;

	/**
	 * Will be null if the inputs do not specify an edge.
	 */
	@Nullable
	public static CubeRotation find(Direction bottom, Direction back) {
		return CubeRotationHelper.find(bottom, back);
	}

	public static final CubeRotation fromOrdinal(int ordinal) {
		return CubeRotationHelper.fromOrdinal(ordinal);
	}

	public static void forEach(Consumer<CubeRotation> consumer) {
		CubeRotationHelper.forEach(consumer);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public CubeRotation rotate(Rotation rotation) {
		final Direction newBack = rotation.rotate(back);
		final Direction newBottom = rotation.rotate(bottom);
		return find(newBottom, newBack);
	}
}
