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

import java.util.Arrays;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

import grondag.fermion.Fermion;
import grondag.fermion.varia.Useful;

public class BitPacker32<T> {
	protected int totalBitLength;
	protected int bitMask;
	protected final ObjIntConsumer<T> writer;
	protected final ToIntFunction<T> reader;

	public BitPacker32(ToIntFunction<T> reader, ObjIntConsumer<T> writer) {
		this.reader = reader;
		this.writer = writer;
	}

	protected void addElement(BitElement element) {
		element.shift = this.totalBitLength;
		element.shiftedMask = element.mask << element.shift;
		element.shiftedInverseMask = ~element.shiftedMask;

		this.totalBitLength += element.bitLength;
		this.bitMask = Useful.intBitMask(totalBitLength);
		if (totalBitLength > 32) {
			Fermion.LOG.error(
					"BitPacker length exceeded. This is definately a bug, and should be impossible in released code. Some things probably won't work correctly.",
					new Exception("BitPacker32 overflow"));
		}
	}

	public final int bitLength() {
		return this.totalBitLength;
	}

	public final int bitMask() {
		return this.bitMask;
	}

	public <V extends Enum<?>> EnumElement<V> createEnumElement(Class<V> e) {
		final EnumElement<V> result = new EnumElement<>(e);
		this.addElement(result);
		return result;
	}

	public <V extends Enum<?>> NullableEnumElement<V> createNullableEnumElement(Class<V> e) {
		final NullableEnumElement<V> result = new NullableEnumElement<>(e);
		this.addElement(result);
		return result;
	}

	public IntElement createIntElement(int minValue, int maxValue) {
		final IntElement result = new IntElement(minValue, maxValue);
		this.addElement(result);
		return result;
	}

	/**
	 * use this when you just need zero-based positive integers. Same as
	 * createIntElement(0, count-1)
	 */
	public IntElement createIntElement(int valueCount) {
		final IntElement result = new IntElement(0, valueCount - 1);
		this.addElement(result);
		return result;
	}

	public BooleanElement createBooleanElement() {
		final BooleanElement result = new BooleanElement();
		this.addElement(result);
		return result;
	}

	protected abstract class BitElement {
		protected final int bitLength;
		protected int mask;
		protected int shift;
		protected int shiftedMask;
		protected int shiftedInverseMask;

		private BitElement(int valueCount) {
			this.bitLength = Useful.bitLength(valueCount);
			this.mask = Useful.intBitMask(this.bitLength);
		}

		/**
		 * Mask that isolates bits for this element. Useful to compare this and other
		 * elements simultaneously
		 */
		public final int comparisonMask() {
			return this.shiftedMask;
		}
	}

	public class EnumElement<V extends Enum<?>> extends BitElement {
		private final V[] values;

		private EnumElement(Class<V> e) {
			super(e.getEnumConstants().length);
			this.values = e.getEnumConstants();
		}

		public final int getBits(V e) {
			return (e.ordinal() & mask) << shift;
		}

		public final void setValue(V e, T inObject) {
			writer.accept(inObject, setValue(e, reader.applyAsInt(inObject)));
		}

		public final int setValue(V e, int inBits) {
			return ((inBits & shiftedInverseMask) | getBits(e));
		}

		public final V getValue(T fromObject) {
			return getValue(reader.applyAsInt(fromObject));
		}

		public final V getValue(long fromBits) {
			return values[(int) ((fromBits >> shift) & mask)];
		}
	}

	public class NullableEnumElement<V extends Enum<?>> extends BitElement {
		private final V[] values;

		final int nullOrdinal;

		private NullableEnumElement(Class<V> e) {
			super(e.getEnumConstants().length + 1);
			final V[] constants = e.getEnumConstants();
			this.values = Arrays.copyOf(constants, constants.length + 1);
			this.nullOrdinal = constants.length;
		}

		public final int getBits(V e) {
			final int ordinal = e == null ? this.nullOrdinal : e.ordinal();
			return (ordinal & mask) << shift;
		}

		public final void setValue(V e, T inObject) {
			writer.accept(inObject, setValue(e, reader.applyAsInt(inObject)));
		}

		public final int setValue(V e, int inBits) {
			return ((inBits & shiftedInverseMask) | getBits(e));
		}

		public final V getValue(T fromObject) {
			return getValue(reader.applyAsInt(fromObject));
		}

		public final V getValue(int fromBits) {
			return values[(fromBits >> shift) & mask];
		}
	}

	/** Stores values in given range as bits. Handles negative values */
	public class IntElement extends BitElement {
		private final int minValue;

		private IntElement(int minValue, int maxValue) {
			super(maxValue - minValue + 1);
			this.minValue = minValue;
		}

		public final int getBits(int i) {
			return ((i - minValue) & mask) << shift;
		}

		public final void setValue(int i, T inObject) {
			writer.accept(inObject, setValue(i, reader.applyAsInt(inObject)));
		}

		public final int setValue(int i, int inBits) {
			return ((inBits & shiftedInverseMask) | getBits(i));
		}

		public final int getValue(T fromObject) {
			return getValue(reader.applyAsInt(fromObject));
		}

		public final int getValue(int fromBits) {
			return ((fromBits >> shift) & mask) + minValue;
		}
	}

	public class BooleanElement extends BitElement {
		private BooleanElement() {
			super(2);
		}

		public final int getBits(boolean b) {
			return ((b ? 1 : 0) & mask) << shift;
		}

		public final void setValue(boolean b, T inObject) {
			writer.accept(inObject, setValue(b, reader.applyAsInt(inObject)));
		}

		public final int setValue(boolean b, int inBits) {
			return ((inBits & shiftedInverseMask) | getBits(b));
		}

		public final boolean getValue(T fromObject) {
			return getValue(reader.applyAsInt(fromObject));
		}

		public final boolean getValue(int fromBits) {
			return ((fromBits >> shift) & mask) == 1;
		}
	}
}
