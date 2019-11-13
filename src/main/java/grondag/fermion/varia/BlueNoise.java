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

import java.util.BitSet;
import java.util.Random;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.util.math.Vec3i;

/**
 * Generates tileable 2d blue noise with integer coordinates using Bridson
 * algorithm for Poisson disk sampling. Representation is sparse so can hold a
 * fairly large tile without too much memory usage but generation time will be
 * correspondingly longer. (Relative to size ^ 2)
 */
public class BlueNoise {
	private final IntArraySet points = new IntArraySet();
	public final int size;
	public final int minSpacing;

	public static BlueNoise create(int size, int minSpacing, long seed) {
		return new BlueNoise(size, minSpacing, seed);
	}

	private BlueNoise(int size, int minSpacing, long seed) {
		this.size = size;
		this.minSpacing = minSpacing;
		generate(seed);
	}

	private class Point {
		private final int x;
		private final int y;

		private Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		private Point(int index) {
			x = xFromIndex(index);
			y = yFromIndex(index);
		}

		private int index() {
			return indexOf(x, y);
		}
	}

	private void generate(long seed) {
		final Random r = new Random(seed);

		final IntArrayList active = new IntArrayList();

		final BitSet points = new BitSet(size * size);

		{
			final int first = indexOf(r.nextInt(), r.nextInt());
			active.add(first);
			points.set(first);
		}

		while (!active.isEmpty()) {
			final Point subject = new Point(active.removeInt(r.nextInt(active.size())));
			for (int i = 0; i < 60; i++) {
				final Point trial = generatePointAround(subject, r);
				if (pointIsValid(trial, points)) {
					active.add(subject.index());
					final int trialIndex = trial.index();
					active.add(trialIndex);
					points.set(trialIndex);
					break;
				}
			}
		}

		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				final int index = indexOf(x, y);
				if (points.get(index)) {
					this.points.add(index);
				}
			}
		}
	}

	private boolean pointIsValid(Point p, BitSet points) {
		if (minSpacing <= 64) {
			// faster to use pre-computed offsets if feasible
			for (final Vec3i vec : Useful.DISTANCE_SORTED_CIRCULAR_OFFSETS) {
				if (vec.getY() > minSpacing) {
					break;
				}

				if (points.get(indexOf(p.x + vec.getX(), p.y + vec.getZ())))
					return false;
			}
		} else {
			for (int j = -minSpacing; j <= minSpacing; j++) {
				for (int k = -minSpacing; k <= minSpacing; k++) {
					if (points.get(indexOf(p.x + j, p.y + k)) && Math.sqrt(j * j + k * k) <= minSpacing)
						return false;
				}
			}
		}
		return true;
	}

	private Point generatePointAround(Point p, Random r) {
		final int radius = minSpacing + r.nextInt(minSpacing + 1);
		final double angle = 2 * Math.PI * r.nextDouble();
		final int newX = (int) Math.round(p.x + radius * Math.cos(angle));
		final int newY = (int) Math.round(p.y + radius * Math.sin(angle));
		return new Point(newX, newY);
	}

	/**
	 * Coordinates are wrapped to size of noise.
	 */
	public boolean isSet(int x, int y) {
		return points.contains(indexOf(x, y));
	}

	//    public void set(int x, int y)
	//    {
	//        this.points.add(indexOf(x, y));
	//    }
	//
	//    public void clear(int x, int y)
	//    {
	//        this.points.rem(indexOf(x, y));
	//    }

	private int xFromIndex(int index) {
		return index & 0xFFFF;
	}

	private int yFromIndex(int index) {
		return index >> 16;
	}

	private int indexOf(int x, int y) {
		x = (x % size + size) % size;
		y = (y % size + size) % size;
		return (y << 16) | x;
	}
}
