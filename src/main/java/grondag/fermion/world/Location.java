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

package grondag.fermion.world;

import grondag.fermion.serialization.NBTDictionary;
import it.unimi.dsi.fastutil.HashCommon;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class Location extends BlockPos {
    private static final String NBT_DIMENSION = NBTDictionary.claim("locDim");
    private static final String NBT_POSITION = NBTDictionary.claim("locPos");

    public static interface ILocated {
        public Location getLocation();

        public void setLocation(Location loc);

        public default void setLocation(BlockPos pos, World world) {
            this.setLocation(new Location(pos, world));
        }

        public default boolean hasLocation() {
            return this.getLocation() != null;
        }

        public default void serializeLocation(CompoundTag tag) {
            saveToNBT(this.getLocation(), tag);
        }

        public default void deserializeLocation(CompoundTag tag) {
            this.setLocation(fromNBT(tag));
        }
    }

    public static void saveToNBT(Location loc, CompoundTag nbt) {
        if (loc != null) {
            nbt.putInt(NBT_DIMENSION, loc.dimensionID);
            nbt.putLong(NBT_POSITION, PackedBlockPos.pack(loc));
        }
    }

    public static Location fromNBT(CompoundTag nbt) {
        if (nbt != null && nbt.containsKey(NBT_POSITION)) {
            int dim = nbt.getInt(NBT_DIMENSION);
            long pos = nbt.getLong(NBT_POSITION);
            return new Location(PackedBlockPos.getX(pos), PackedBlockPos.getY(pos), PackedBlockPos.getZ(pos), dim);
        } else {
            return null;
        }
    }

    private final int dimensionID;

    public Location(int x, int y, int z, int dimensionID) {
        super(x, y, z);
        this.dimensionID = dimensionID;
    }

    public Location(int x, int y, int z, World world) {
        this(x, y, z, world.getDimension().getType().getRawId());
    }

    public Location(BlockPos pos, World world) {
        this(pos.getX(), pos.getY(), pos.getZ(), world.getDimension().getType().getRawId());
    }

    public Location(BlockPos pos, int dimensionID) {
        this(pos.getX(), pos.getY(), pos.getZ(), dimensionID);
    }

    public int dimensionID() {
        return this.dimensionID;
    }

    public DimensionType dimension() {
        return DimensionType.byRawId(this.dimensionID);
    }

    public World world() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                ? ((MinecraftClient)FabricLoader.getInstance().getGameInstance()).getServer().getWorld(dimension())
                : ((MinecraftDedicatedServer)FabricLoader.getInstance().getGameInstance()).getWorld(dimension());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Location))
            return false;
        Location other = (Location) o;
        return this.getX() == other.getX() && this.getY() == other.getY() && this.getZ() == other.getZ()
                && this.dimensionID == other.dimensionID;
    }

    @Override
    public int hashCode() {
        return (int) HashCommon.mix((long) super.hashCode() | (this.dimensionID << 32));
    }
}
