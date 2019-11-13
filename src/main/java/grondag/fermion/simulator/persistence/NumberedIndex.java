package grondag.fermion.simulator.persistence;

import grondag.fermion.Fermion;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SuppressWarnings("serial")
public class NumberedIndex extends Int2ObjectOpenHashMap<Numbered> {
	public final String numberType;
	private final DirtNotifier dirtNotifier;
	int lastId = Numbered.LAST_SYSTEM_NUM;

	NumberedIndex(String numberType, DirtNotifier dirtNotifier) {
		this.numberType = numberType;
		this.dirtNotifier = dirtNotifier;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	void reset(int lastIndex) {
		super.clear();
		lastId = lastIndex;
	}

	public synchronized void register(Numbered thing) {
		final Numbered prior = this.put(thing.getAssignedNumber(), thing);

		if (prior != null && !prior.equals(thing)) {
			Fermion.LOG.warn("Assigned number index overwrote registered object due to index collision.  This is a bug.");
		}
	}

	public synchronized void unregister(Numbered thing) {
		final Numbered prior = this.remove(thing.getAssignedNumber());
		if (prior == null || !prior.equals(thing)) {
			Fermion.LOG.warn("Assigned number index unregistered wrong object due to index collision.  This is a bug.");
		}
	}

	@Override
	public synchronized Numbered get(int index) {
		return super.get(index);
	}

	/**
	 * First ID returned for each type is 1000 to allow room for system IDs. System
	 * ID's should start at 1 to distinguish from missing/unset ID.
	 */
	public synchronized int newNumber() {
		dirtNotifier.markDirty();
		return ++lastId;
	}
}