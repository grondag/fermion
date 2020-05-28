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

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.util.function.Consumer;

import org.apiguardian.api.API;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

@API(status = EXPERIMENTAL)
public enum ClockwiseRotation implements StringIdentifiable {
	ROTATE_NONE(0),
	ROTATE_90(90),
	ROTATE_180(180),
	ROTATE_270(270);

	public final String name;

	/**
	 * Useful for locating model file names that use degrees as a suffix.
	 */
	public final int degrees;

	/**
	 * Opposite of degress - useful for GL transforms. 0 and 180 are same, 90 and
	 * 270 are flipped
	 */
	public final int degreesInverse;

	private ClockwiseRotation(int degrees) {
		name = name().toLowerCase();
		this.degrees = degrees;
		degreesInverse = (360 - degrees) % 360;
	}

	@Override
	public String asString() {
		return name;
	}

	public ClockwiseRotation clockwise() {
		switch (this) {
		case ROTATE_180:
			return ROTATE_270;
		case ROTATE_270:
			return ROTATE_NONE;
		case ROTATE_90:
			return ROTATE_180;
		case ROTATE_NONE:
		default:
			return ROTATE_90;
		}
	}

	private static final ClockwiseRotation[] VALUES = ClockwiseRotation.values();
	public static final int COUNT = VALUES.length;
	private static ClockwiseRotation[] FROM_HORIZONTAL_FACING = new ClockwiseRotation[6];

	static {
		FROM_HORIZONTAL_FACING[Direction.NORTH.ordinal()] = ROTATE_180;
		FROM_HORIZONTAL_FACING[Direction.EAST.ordinal()] = ROTATE_270;
		FROM_HORIZONTAL_FACING[Direction.SOUTH.ordinal()] = ROTATE_NONE;
		FROM_HORIZONTAL_FACING[Direction.WEST.ordinal()] = ROTATE_90;
		FROM_HORIZONTAL_FACING[Direction.UP.ordinal()] = ROTATE_NONE;
		FROM_HORIZONTAL_FACING[Direction.DOWN.ordinal()] = ROTATE_NONE;
	}

	/**
	 * Gives the rotation with horiztonalFace matching the given NSEW face For up
	 * and down will return ROTATE_NONE
	 */
	public static ClockwiseRotation fromHorizontalFacing(Direction face) {
		return FROM_HORIZONTAL_FACING[face.ordinal()];
	}

	public static final ClockwiseRotation fromOrdinal(int ordinal) {
		return VALUES[ordinal];
	}

	public static void forEach(Consumer<ClockwiseRotation> consumer) {
		for (final ClockwiseRotation val : VALUES) {
			consumer.accept(val);
		}
	}
}
