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

import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Registrar extends AbstractRegistrar {
	public final ItemGroup itemGroup;

	public Registrar(String modId, String groupItemName) {
		super(modId);
		final Identifier itemGroupItemId = new Identifier(modId, groupItemName);
		itemGroup = FabricItemGroupBuilder.build(new Identifier(modId, "group"), () -> new ItemStack(Registry.ITEM.get(itemGroupItemId)));
	}

	public Item.Settings itemSettings() {
		return new Item.Settings().group(itemGroup);
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

	public <T extends Block> T block(String name, T block, Item.Settings settings) {
		return block(name, block, new BlockItem(block, settings));
	}

	public <T extends Block> T block(String name, T block) {
		return block(name, block, itemSettings());
	}

	public <T extends Block> T block(String name, T block, Function<T, BlockItem> itemFactory) {
		return block(name, block, itemFactory.apply(block));
	}

	public <T extends Block> T block(String name, T block, BlockItem item) {
		final T b = Registry.register(Registry.BLOCK, id(name), block);
		if (item != null) {
			final BlockItem bi = item(name, item);
			bi.appendBlocks(BlockItem.BLOCK_ITEMS, bi);
		}
		return b;
	}

	public <T extends Block> T blockNoItem(String name, T block) {
		final T b = Registry.register(Registry.BLOCK, id(name), block);
		return b;
	}

	public <T extends BlockEntity> BlockEntityType<T> blockEntityType(String id, Supplier<T> supplier, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY, id(id), BlockEntityType.Builder.create(supplier, blocks).build(null));
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
		final Identifier idid = id(id);
		return Registry.register(Registry.SOUND_EVENT, idid, new SoundEvent(idid));
	}

	public Tag<Fluid> fluidTag(String id) {
		return TagRegistry.fluid(id(id));
	}

	public Tag<Block> blockTag(String id) {
		return TagRegistry.block(id(id));
	}

	public Tag<Item> itemTag(String id) {
		return TagRegistry.item(id(id));
	}

	public Tag<EntityType<?>> entityTag(String id) {
		return TagRegistry.entityType(id(id));
	}

	public DefaultParticleType particle(String id, boolean alwaysSpawn) {
		return Registry.register(Registry.PARTICLE_TYPE, id(id), FabricParticleTypes.simple(alwaysSpawn));
	}

	public <T extends ParticleEffect> ParticleType<T> particle(String id, boolean alwaysSpawn, ParticleEffect.Factory<T> factory)  {
		return Registry.register(Registry.PARTICLE_TYPE, id(id), FabricParticleTypes.complex(alwaysSpawn, factory));
	}

	public StatusEffect statusEffect(String id, StatusEffect effect) {
		return Registry.register(Registry.STATUS_EFFECT, id(id), effect);
	}
}
