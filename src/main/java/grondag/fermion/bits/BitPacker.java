package grondag.fermion.bits;


import java.util.Arrays;

import net.minecraft.nbt.CompoundTag;

import grondag.fermion.varia.Useful;

public class BitPacker {
	private int lastIndex = 0;

	public static class PackedState {
		private final long[] bits;

		private PackedState(int size) {
			bits = new long[size];
		}

		public void writeNbt(String tagName, CompoundTag toTag) {
			toTag.putLongArray(tagName, bits);
		}

		public void readNbt(String tagName, CompoundTag fromTag) {
			Arrays.fill(bits, 0);
			final long[] src = fromTag.getLongArray(tagName);

			if (src != null) {
				final int len = Math.min(src.length, bits.length);
				System.arraycopy(src, 0, bits, 0, len);
			}
		}

		public boolean areBitEqual(PackedState state) {
			return Arrays.equals(bits, state.bits);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(bits);
		}
	}

	public PackedState newState() {
		return new PackedState(lastIndex + 1);
	}

	private BitPacker64<BitPacker> openPacker = new BitPacker64<>(null, null);


	private BitPacker64<BitPacker> packerForSize(int bitCount) {
		assert bitCount <= 64;

		if (openPacker.bitLength() + bitCount > 64) {
			openPacker = new BitPacker64<>(null, null);
			++lastIndex;
		}

		return openPacker;
	}

	public IntElement createIntElement(int minValue, int maxValue) {
		final BitPacker64<BitPacker> packer = packerForSize(Useful.bitLength(maxValue - minValue + 1));
		return new IntElement(packer.createIntElement(minValue, maxValue), lastIndex);
	}

	/**
	 * use this when you just need zero-based positive integers. Same as
	 * createIntElement(0, count-1)
	 */
	public IntElement createIntElement(int valueCount) {
		return createIntElement(0, valueCount - 1);
	}

	public class IntElement {
		private final BitPacker64<BitPacker>.IntElement packer;
		private final int bitIndex;

		private IntElement(BitPacker64<BitPacker>.IntElement packer, int bitIndex) {
			this.packer = packer;
			this.bitIndex = bitIndex;
		}
		public int get(PackedState fromState) {
			return packer.getValue(fromState.bits[bitIndex]);
		}

		public void set(PackedState toState, int value) {
			toState.bits[bitIndex] = packer.setValue(value, toState.bits[bitIndex]);
		}
	}

	public class BitElement {
		private final BitPacker64<BitPacker>.BooleanElement packer;
		private final int bitIndex;

		private BitElement(BitPacker64<BitPacker>.BooleanElement packer, int bitIndex) {
			this.packer = packer;
			this.bitIndex = bitIndex;
		}
		public boolean get(PackedState fromState) {
			return packer.getValue(fromState.bits[bitIndex]);
		}

		public void set(PackedState toState, boolean value) {
			toState.bits[bitIndex] = packer.setValue(value, toState.bits[bitIndex]);
		}
	}
}
