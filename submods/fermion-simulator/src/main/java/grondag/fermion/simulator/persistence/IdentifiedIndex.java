package grondag.fermion.simulator.persistence;

import grondag.fermion.Fermion;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.zeroeightsix.fiber.Identifier;

@SuppressWarnings("serial")
public class IdentifiedIndex extends Int2ObjectOpenHashMap<Identified> {
	public final Identifier numberType;

	IdentifiedIndex(Identifier numberType) {
		this.numberType = numberType;
	}

	public synchronized void register(Identified thing) {
		final Identified prior = this.put(thing.getId(), thing);

		if (prior != null && !prior.equals(thing)) {
			Fermion.LOG.warn("Assigned number index overwrote registered object due to index collision.  This is a bug.");
		}
	}

	public synchronized void unregister(Identified thing) {
		final Identified prior = this.remove(thing.getId());
		if (prior == null || !prior.equals(thing)) {
			Fermion.LOG.warn("Assigned number index unregistered wrong object due to index collision.  This is a bug.");
		}
	}

	@Override
	public synchronized Identified get(int index) {
		return super.get(index);
	}
}