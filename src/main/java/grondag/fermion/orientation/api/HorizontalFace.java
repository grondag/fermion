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
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.Nullable;
import grondag.fermion.orientation.impl.HorizontalFaceHelper;

/**
 * A subset of {@link Direction}, includes only the face in the horizontal
 * plane.
 */
@Experimental
public enum HorizontalFace implements StringRepresentable {
	NORTH(Direction.NORTH),
	EAST(Direction.EAST),
	SOUTH(Direction.SOUTH),
	WEST(Direction.WEST);

	public final Direction face;

	public final Vec3i vector;

	public final String name;

	HorizontalFace(Direction face) {
		name = name().toLowerCase(Locale.ROOT);
		this.face = face;
		vector = face.getNormal();
	}

	public HorizontalFace left() {
		if (ordinal() == 0)
			return HorizontalFace.values()[3];
		else
			return HorizontalFace.values()[ordinal() - 1];
	}

	public HorizontalFace right() {
		if (ordinal() == 3)
			return HorizontalFace.values()[0];
		else
			return HorizontalFace.values()[ordinal() + 1];
	}

	public static final int COUNT = HorizontalFaceHelper.COUNT;

	/**
	 * Will return null if input is not a horizontal face.
	 */
	@Nullable
	public static HorizontalFace find(Direction face) {
		return HorizontalFaceHelper.find(face);
	}

	public static final HorizontalFace fromOrdinal(int ordinal) {
		return HorizontalFaceHelper.fromOrdinal(ordinal);
	}

	public static void forEach(Consumer<HorizontalFace> consumer) {
		HorizontalFaceHelper.forEach(consumer);
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}
