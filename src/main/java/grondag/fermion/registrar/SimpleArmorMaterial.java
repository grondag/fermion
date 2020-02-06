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

import java.util.function.Supplier;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Lazy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class SimpleArmorMaterial implements ArmorMaterial {
	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] protectionAmounts;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final float toughness;
	private final Lazy<Ingredient> repairIngredientSupplier;

	public SimpleArmorMaterial(String id, int durability, int[] protection, int enchantability, SoundEvent soundEvent, float toughness, Supplier<Ingredient> supplier) {
		name = id;
		durabilityMultiplier = durability;
		protectionAmounts = protection;
		this.enchantability = enchantability;
		equipSound = soundEvent;
		this.toughness = toughness;
		repairIngredientSupplier = new Lazy<>(supplier);
	}

	@Override
	public int getDurability(EquipmentSlot equipmentSlot) {
		return BASE_DURABILITY[equipmentSlot.getEntitySlotId()] * durabilityMultiplier;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot equipmentSlot) {
		return protectionAmounts[equipmentSlot.getEntitySlotId()];
	}

	@Override
	public int getEnchantability() {
		return enchantability;
	}

	@Override
	public SoundEvent getEquipSound() {
		return equipSound;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repairIngredientSupplier.get();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public String getName() {
		return name;
	}

	@Override
	public float getToughness() {
		return toughness;
	}

	public static ArmorMaterial of(String id, int durability, int[] protection, int enchantability, SoundEvent soundEvent, float toughness, Supplier<Ingredient> supplier) {
		return new SimpleArmorMaterial(id, durability, protection, enchantability, soundEvent, toughness, supplier);
	}

	public static ArmorMaterial of(String id, int durability, int[] protection, int enchantability, SoundEvent soundEvent, float toughness, ItemConvertible repairItem) {
		return new SimpleArmorMaterial(id, durability, protection, enchantability, soundEvent, toughness, () -> Ingredient.ofItems(repairItem.asItem()));
	}

	@Override
	public float method_24355() {
		// TODO implement knock-back resistance
		return 0;
	}
}
