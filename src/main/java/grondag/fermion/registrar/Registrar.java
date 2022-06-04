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

package grondag.fermion.registrar;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

public class Registrar extends AbstractRegistrar {
	public final CreativeModeTab itemGroup;

	public Registrar(String modId, String groupItemName) {
		super(modId);
		final ResourceLocation itemGroupItemId = new ResourceLocation(modId, groupItemName);
		itemGroup = FabricItemGroupBuilder.build(new ResourceLocation(modId, "group"), () -> new ItemStack(Registry.ITEM.get(itemGroupItemId)));
	}

	public Item.Properties itemSettings() {
		return new Item.Properties().tab(itemGroup);
	}

	public <T extends Item> T item(String name, T item) {
		return Registry.register(Registry.ITEM, id(name), item);
	}

	public Item item(String name) {
		return item(name, new Item(itemSettings()));
	}

	public ArmorItem armorItem(String name, ArmorMaterial material, EquipmentSlot slot) {
		return item(name, new ArmorItem(material, slot, itemSettings()));
	}

	public <T extends Fluid> T fluid(String name, T fluid) {
		final T b = Registry.register(Registry.FLUID, id(name), fluid);
		return b;
	}

	public <T extends Block> T block(String name, T block, Item.Properties settings) {
		return block(name, block, new BlockItem(block, settings));
	}

	public <T extends Block> T block(String name, T block) {
		return block(name, block, itemSettings());
	}

	public <T extends Block> T block(String name, T block, Function<T, BlockItem> itemFactory) {
		return block(name, block, itemFactory.apply(block));
	}

	public <T extends Block> T block(String name, T block, BiFunction<T, Item.Properties, BlockItem> itemFactory) {
		return block(name, block, itemFactory.apply(block, itemSettings()));
	}

	public <T extends Block> T block(String name, T block, BlockItem item) {
		final T b = Registry.register(Registry.BLOCK, id(name), block);
		if (item != null) {
			final BlockItem bi = item(name, item);
			bi.registerBlocks(BlockItem.BY_BLOCK, bi);
		}
		return b;
	}

	public <T extends Block> T blockNoItem(String name, T block) {
		final T b = Registry.register(Registry.BLOCK, id(name), block);
		return b;
	}

	public <T extends BlockEntity> BlockEntityType<T> blockEntityType(String id, FabricBlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, id(id), FabricBlockEntityTypeBuilder.create(supplier, blocks).build(null));
	}

	public <T extends RecipeSerializer<?>> T recipeSerializer(String id, T serializer) {
		return Registry.register(Registry.RECIPE_SERIALIZER, id(id), serializer);
	}

	public <T extends Recipe<?>> RecipeType<T> recipeType(String id) {
		return Registry.register(Registry.RECIPE_TYPE, id(id), new RecipeType<T>() {
			@Override
			public String toString() {
				return id;
			}
		});
	}

	public SoundEvent sound(String id) {
		final ResourceLocation idid = id(id);
		return Registry.register(Registry.SOUND_EVENT, idid, new SoundEvent(idid));
	}

	public SimpleParticleType particle(String id, boolean alwaysSpawn) {
		return Registry.register(Registry.PARTICLE_TYPE, id(id), FabricParticleTypes.simple(alwaysSpawn));
	}

	public <T extends ParticleOptions> ParticleType<T> particle(String id, boolean alwaysSpawn, ParticleOptions.Deserializer<T> factory)  {
		return Registry.register(Registry.PARTICLE_TYPE, id(id), FabricParticleTypes.complex(alwaysSpawn, factory));
	}

	public MobEffect statusEffect(String id, MobEffect effect) {
		return Registry.register(Registry.MOB_EFFECT, id(id), effect);
	}
}
