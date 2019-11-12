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

package grondag.fermion.bits;

import java.util.function.IntConsumer;

public class BitHelper {
	public static void forEachBit(long bits, IntConsumer consumer) {
		if (bits != 0) {
			forEachBit32((int) (bits & 0xFFFFFFFFL), 0, consumer);
			forEachBit32((int) ((bits >>> 32) & 0xFFFFFFFFL), 32, consumer);
		}
	}

	private static void forEachBit32(int bits, int baseIndex, IntConsumer consumer) {
		if (bits != 0) {
			forEachBit16((bits & 0xFFFF), baseIndex, consumer);
			forEachBit16(((bits >>> 16) & 0xFFFF), baseIndex + 16, consumer);
		}
	}

	private static void forEachBit16(int bits, int baseIndex, IntConsumer consumer) {
		if (bits != 0) {
			forEachBit8((bits & 0xFF), baseIndex, consumer);
			forEachBit8(((bits >>> 8) & 0xFF), baseIndex + 8, consumer);
		}
	}

	private static void forEachBit8(int bits, int baseIndex, IntConsumer consumer) {
		if (bits != 0) {
			forEachBit4((bits & 0xF), baseIndex, consumer);
			forEachBit4(((bits >>> 4) & 0xF), baseIndex + 4, consumer);
		}
	}

	private static void forEachBit4(int bits, int baseIndex, IntConsumer consumer) {
		switch (bits) {
		case 0:
			break;

		case 1:
			consumer.accept(baseIndex);
			break;

		case 2:
			consumer.accept(baseIndex + 1);
			break;

		case 3:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 1);
			break;

		case 4:
			consumer.accept(baseIndex + 2);
			break;

		case 5:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 2);
			break;

		case 6:
			consumer.accept(baseIndex + 1);
			consumer.accept(baseIndex + 2);
			break;

		case 7:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 1);
			consumer.accept(baseIndex + 2);
			break;

		case 8:
			consumer.accept(baseIndex + 3);
			break;

		case 9:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 3);
			break;

		case 10:
			consumer.accept(baseIndex + 1);
			consumer.accept(baseIndex + 3);
			break;

		case 11:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 1);
			consumer.accept(baseIndex + 3);
			break;

		case 12:
			consumer.accept(baseIndex + 2);
			consumer.accept(baseIndex + 3);
			break;

		case 13:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 2);
			consumer.accept(baseIndex + 3);
			break;

		case 14:
			consumer.accept(baseIndex + 1);
			consumer.accept(baseIndex + 2);
			consumer.accept(baseIndex + 3);
			break;

		case 15:
			consumer.accept(baseIndex);
			consumer.accept(baseIndex + 1);
			consumer.accept(baseIndex + 2);
			consumer.accept(baseIndex + 3);
			break;
		}
	}

	public static int bitCount8(int byteValue) {
		return bitCount4(byteValue & 0xF) + bitCount4((byteValue >>> 4) & 0xF);
	}

	public static int bitCount4(int halfByteValue) {
		switch (halfByteValue) {
		case 0:
			return 0;

		case 1:
		case 2:
		case 4:
		case 8:
			return 1;

		case 3:
		case 5:
		case 6:
		case 9:
		case 10:
		case 12:
			return 2;

		case 7:
		case 11:
		case 13:
		case 14:
			return 3;

		case 15:
			return 4;
		}
		assert false : "bad bitcount4 value";
		return 0;
	}
}
