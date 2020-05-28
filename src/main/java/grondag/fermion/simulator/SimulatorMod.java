package grondag.fermion.simulator;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;

public class SimulatorMod implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(Simulator::start);
		ServerTickCallback.EVENT.register(s -> Simulator.instance().tick(s));
	}
}
