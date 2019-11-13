package grondag.fermion.position;

import static grondag.fermion.position.PackedBlockPos.getX;
import static grondag.fermion.position.PackedBlockPos.getY;
import static grondag.fermion.position.PackedBlockPos.getZ;
import static grondag.fermion.position.PackedBlockPos.pack;

import it.unimi.dsi.fastutil.longs.LongArrayList;

public class PackedBlockPosList {
	private final LongArrayList list = new LongArrayList();
	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxY = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;

	public void add(long packedPos) {
		list.add(packedPos);

		final int x = getX(packedPos);
		final int y = getY(packedPos);
		final int z = getZ(packedPos);

		if (x < minX) {
			minX = x;
		}
		if (y < minY) {
			minY = y;
		}
		if (z < minZ) {
			minZ = z;
		}

		if (x > maxX) {
			maxX = x;
		}
		if (y > maxY) {
			maxY = y;
		}
		if (z > maxZ) {
			maxZ = z;
		}
	}

	public long get(int index) {
		return list.getLong(index);
	}

	public int size() {
		return list.size();
	}

	public void clear() {
		list.clear();
		minX = Integer.MAX_VALUE;
		minY = Integer.MAX_VALUE;
		minZ = Integer.MAX_VALUE;
		maxX = Integer.MIN_VALUE;
		maxY = Integer.MIN_VALUE;
		maxZ = Integer.MIN_VALUE;
	}

	public long minBound() {
		return pack(minX, minY, minZ);
	}

	/** inclusive */
	public long maxBound() {
		return pack(maxX, maxY, maxZ);
	}

	public boolean isInBounds(long packedPos) {
		final int x = getX(packedPos);
		final int y = getY(packedPos);
		final int z = getZ(packedPos);

		return !(x < minX || x > maxX || y < minY || y > maxY || z < minZ || z > maxZ);
	}

	public boolean isNear(long packedPos, int d) {
		return true;
		//TODO: put back
		//		final int x = getX(packedPos);
		//		final int y = getY(packedPos);
		//		final int z = getZ(packedPos);
		//
		//		return !(x < minX - d || x > maxX + d || y < minY - d|| y > maxY + d || z < minZ - d || z > maxZ + d);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
