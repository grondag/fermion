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
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.Nullable;
import grondag.fermion.orientation.impl.HorizontalEdgeHelper;

/**
 * A subset of {@link CubeEdge}, includes only the edges in the horizontal
 * plane.
 */
@Experimental
public enum HorizontalEdge implements StringRepresentable {
	NORTH_EAST(HorizontalFace.NORTH, HorizontalFace.EAST),
	NORTH_WEST(HorizontalFace.WEST, HorizontalFace.NORTH),
	SOUTH_EAST(HorizontalFace.EAST, HorizontalFace.SOUTH),
	SOUTH_WEST(HorizontalFace.SOUTH, HorizontalFace.WEST);

	public final HorizontalFace left;
	public final HorizontalFace right;

	public final Vec3i vector;

	public final String name;

	HorizontalEdge(HorizontalFace left, HorizontalFace right) {
		name = name().toLowerCase(Locale.ROOT);
		this.left = left;
		this.right = right;
		vector = new Vec3i(left.face.getNormal().getX() + right.face.getNormal().getX(), 0,
			left.face.getNormal().getZ() + right.face.getNormal().getZ());
	}

	public HorizontalEdge rotate(Rotation rotation) {
		final Direction face1 = rotation.rotate(left.face);
		final Direction face2 = rotation.rotate(right.face);
		return ObjectUtils.defaultIfNull(find(face1, face2), this);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public static final int COUNT = HorizontalEdgeHelper.COUNT;

	/**
	 * Will return null if inputs do not specify a horizontal block edge.
	 */
	@Nullable
	public static HorizontalEdge find(HorizontalFace face1, HorizontalFace face2) {
		return HorizontalEdgeHelper.find(face1, face2);
	}

	@Nullable
	public static HorizontalEdge find(Direction face1, Direction face2) {
		return find(HorizontalFace.find(face1), HorizontalFace.find(face2));
	}

	public static HorizontalEdge fromRotation(double yawDegrees) {
		return HorizontalEdgeHelper.fromRotation(yawDegrees);
	}

	public static HorizontalEdge fromOrdinal(int ordinal) {
		return HorizontalEdgeHelper.fromOrdinal(ordinal);
	}

	public static void forEach(Consumer<HorizontalEdge> consumer) {
		HorizontalEdgeHelper.forEach(consumer);
	}
}
