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
package grondag.fermion.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import grondag.fermion.Fermion;
import grondag.fermion.sc.concurrency.ScatterGatherThreadPool;
import grondag.fermion.simulator.persistence.AssignedNumbersAuthority;
import grondag.fermion.simulator.persistence.DirtKeeper;
import grondag.fermion.simulator.persistence.SimulationTopNode;
import grondag.fermion.varia.NBTDictionary;

/**
 * Events are processed from a queue in the order they arrive.
 *
 * World events are always added to the queue as soon as they arrive.
 *
 * Simulation ticks are generated as the world clock advances at the rate of one
 * simulation tick per world tick.
 *
 * Simulation ticks are added to the queue by a privileged task that is added at
 * the end of simulation tick. No new simulation ticks are added until all tasks
 * in the last tick are complete.
 *
 * No simulation ticks are ever skipped. This means that if players sleep and
 * the work clock advances, the simulation will continue running as quickly as
 * possible until caught up.
 *
 * However, world events will continue to be processed as soon as they arrive.
 * This means that a player waking up and interacting with machines immediately
 * may not see that all processing is complete but will observe that the
 * machines are running very quickly.
 *
 */
public class Simulator extends PersistentState implements DirtKeeper {

	////////////////////////////////////////////////////////////
	// STATIC MEMBERS
	////////////////////////////////////////////////////////////

	private static final String NBT_TAG_SIMULATOR = NBTDictionary.GLOBAL.claim("emSimulator");
	private static final String NBT_TAG_LAST_TICK = NBTDictionary.GLOBAL.claim("simLastTick");
	private static final String NBT_TAG_WORLD_TICK_OFFSET = NBTDictionary.GLOBAL.claim("simTickOffset");

	/**
	 * Only use if need a reference before it starts.
	 */
	private static Simulator instance;

	/**
	 * Needed to prevent overhead of retrieving instance each time this is needed.
	 * Needed all over.
	 */
	private static int currentTick;

	/**
	 * Needed to prevent overhead of retrieving instance each time this is needed.
	 * Needed all over.
	 */
	public static final int currentTick() {
		return currentTick;
	}

	private static final HashMap<String, Function<NbtCompound, SimulationTopNode>> nodeTypes = new HashMap<>();

	public static void register(String id, Function<NbtCompound, SimulationTopNode> nodeType) {
		nodeTypes.put(id, nodeType);
	}

	public static final ScatterGatherThreadPool SCATTER_GATHER_POOL = new ScatterGatherThreadPool();

	/**
	 * Main simulation control thread - runs outside server thread.
	 */
	public static final ExecutorService CONTROL_THREAD = Executors.newSingleThreadExecutor(new ThreadFactory() {
		private final AtomicInteger count = new AtomicInteger(1);

		@Override
		public Thread newThread(@Nullable Runnable r) {
			final Thread thread = new Thread(r, "Exotic Matter Simulation Control Thread -" + count.getAndIncrement());
			thread.setDaemon(true);
			return thread;
		}
	});

	/** used for world time */
	private static @Nullable ServerWorld world;

	private static MinecraftServer server;

	public static MinecraftServer server() {
		return server;
	}

	////////////////////////////////////////////////////////////
	// INSTANCE MEMBERS
	////////////////////////////////////////////////////////////

	private final AssignedNumbersAuthority assignedNumbersAuthority = new AssignedNumbersAuthority();

	public AssignedNumbersAuthority assignedNumbersAuthority() {
		return assignedNumbersAuthority;
	}

	private final IdentityHashMap<Class<? extends SimulationTopNode>, SimulationTopNode> nodes = new IdentityHashMap<>();

	private final List<SimulationTickable> tickables = new ArrayList<>();

	private @Nullable Future<?> lastTickFuture = null;

	private volatile boolean isRunning = false;

	public boolean isRunning() {
		return isRunning;
	}

	/** true if we've warned once about clock going backwards - prevents log spam */
	private boolean isClockSetbackNotificationNeeded = true;

	// private AtomicInteger nextNodeID = new
	// AtomicInteger(NodeRoots.FIRST_NORMAL_NODE_ID);
	// private static final String TAG_NEXT_NODE_ID = "nxid";

	/**
	 * Set to worldTickOffset + lastWorldTick at end of server tick. If equal to
	 * currentSimTick, means simulation is caught up with world ticks.
	 */
	private volatile int lastSimTick = 0;

	/**
	 * worldTickOffset + lastWorldTick = max value of current simulation tick.
	 * Updated on server post tick, *after* all world tick events should be
	 * submitted.
	 */
	private volatile long worldTickOffset = 0;

	public Simulator() {
		super();
		// defaults for new simulation - will be overwritten if deserialized from tag
		lastSimTick = 0;
		worldTickOffset = -world.getTime();
	}

	public Simulator(NbtCompound tag) {
		this();
		readNbt(tag);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <V extends SimulationTopNode> V getNode(Class<V> nodeType) {
		return (V) nodes.get(nodeType);
	}

	public static void start(MinecraftServer serverIn) {
		server = serverIn;

		for (final ServerWorld w : serverIn.getWorlds()) {
			if (w.getRegistryKey() == World.OVERWORLD)  {
				world = w;
				break;
			}
		}

		instance = world.getPersistentStateManager().getOrCreate(Simulator::new, Simulator::new, NBT_TAG_SIMULATOR);
		instance.initialize(serverIn);
	}

	private void initialize(MinecraftServer server) {
		synchronized (this) {
			isRunning = true;

			Fermion.LOG.info("Simulator initialization started.");

			assignedNumbersAuthority.clear();
			assignedNumbersAuthority.setDirtKeeper(this);

			tickables.clear();

			nodes.clear();
			nodeTypes.forEach((s, t) -> {
				try {
					final SimulationTopNode node = world.getPersistentStateManager().get(t, s);
					nodes.put(node.getClass(), node);
					node.afterCreated(this);
				} catch (final Exception e) {
					Fermion.LOG.error("Unable to create simulation node " + t.getClass().getName(), e);
					// FIXME: should crash here
				}
			});

			nodes.values().forEach(n -> n.afterDeserialization());

			nodes.values().forEach(n -> {
				if (n instanceof SimulationTickable) {
					tickables.add((SimulationTickable) n);
				}
			});

			Fermion.LOG.info("Simulator initialization complete. Simulator running.");
		}
	}

	/**
	 * Called from ServerStopping event. Should be no more ticks after that.
	 */
	public synchronized void stop() {
		if (!isRunning)
			return;
		Fermion.LOG.info("stopping server");
		isRunning = false;

		// wait for simulation to catch up
		if (lastTickFuture != null && !lastTickFuture.isDone()) {
			Fermion.LOG.info("waiting for last frame task completion");
			try {
				lastTickFuture.get(5, TimeUnit.SECONDS);
			} catch (final Exception e) {
				Fermion.LOG.warn("Timeout waiting for simulation shutdown");
				e.printStackTrace();
			}
		}

		nodes.values().forEach(n -> n.unload());
		nodes.clear();

		world = null;
		server = null;
		lastTickFuture = null;
	}

	public void tick(MinecraftServer server) {
		if (isRunning) {

			if (lastTickFuture == null || lastTickFuture.isDone()) {

				final int newLastSimTick = (int) (world.getTime() + worldTickOffset);

				// Simulation clock can't move backwards.
				// NB: don't need CAS because only ever changed by game thread in this method
				if (newLastSimTick > lastSimTick) {
					// if((newLastSimTick & 31) == 31) HardScience.log.info("changing lastSimTick,
					// old=" + lastSimTick + ", new=" + newLastSimTick);
					lastSimTick = newLastSimTick;
				} else {
					// world clock has gone backwards or paused, so readjust offset
					lastSimTick++;
					worldTickOffset = lastSimTick - world.getTime();
					if (isClockSetbackNotificationNeeded) {
						Fermion.LOG.warn("World clock appears to have run backwards.  Simulation clock offset was adjusted to compensate.");
						Fermion.LOG.warn("Next tick according to world was " + newLastSimTick + ", using " + lastSimTick + " instead.");
						Fermion.LOG.warn("If this recurs, simulation clock will be similarly adjusted without notification.");
						isClockSetbackNotificationNeeded = false;
					}
				}

				currentTick = lastSimTick;

				if (!Simulator.this.tickables.isEmpty()) {
					for (final SimulationTickable tickable : Simulator.this.tickables) {
						tickable.doOnTick();
					}
				}

				lastTickFuture = CONTROL_THREAD.submit(offTickFrame);

				this.makeDirty();
			}
		}
	}

	public void readNbt(NbtCompound nbt) {
		assignedNumbersAuthority.writeTag(nbt);
		lastSimTick = nbt.getInt(NBT_TAG_LAST_TICK);
		worldTickOffset = nbt.getLong(NBT_TAG_WORLD_TICK_OFFSET);
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		assignedNumbersAuthority.readTag(nbt);
		nbt.putInt(NBT_TAG_LAST_TICK, lastSimTick);
		nbt.putLong(NBT_TAG_WORLD_TICK_OFFSET, worldTickOffset);
		return nbt;
	}

	public @Nullable ServerWorld getWorld() {
		return world;
	}

	// Frame execution logic
	Runnable offTickFrame = new Runnable() {
		@Override
		public void run() {
			if (!tickables.isEmpty()) {
				for (final SimulationTickable tickable : tickables) {
					try {
						tickable.doOffTick();
					} catch (final Exception e) {
						Fermion.LOG.error("Exception during simulator off-tick processing", e);
					}
				}
			}
		}
	};

	public static Simulator instance() {
		return instance;

	}

	@Override
	public void makeDirty(boolean isDirty) {
		setDirty(isDirty);
	}
}
