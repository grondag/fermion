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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class SimpleArmorMaterial implements ArmorMaterial {
	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private final String name;
	private final int durabilityMultiplier;
	private final int[] protectionAmounts;
	private final int enchantability;
	private final SoundEvent equipSound;
	private final float toughness;
	private final LazyLoadedValue<Ingredient> repairIngredientSupplier;

	public SimpleArmorMaterial(String id, int durability, int[] protection, int enchantability, SoundEvent soundEvent, float toughness, Supplier<Ingredient> supplier) {
		name = id;
		durabilityMultiplier = durability;
		protectionAmounts = protection;
		this.enchantability = enchantability;
		equipSound = soundEvent;
		this.toughness = toughness;
		repairIngredientSupplier = new LazyLoadedValue<>(supplier);
	}

	@Override
	public int getDurabilityForSlot(EquipmentSlot equipmentSlot) {
		return BASE_DURABILITY[equipmentSlot.getIndex()] * durabilityMultiplier;
	}

	@Override
	public int getDefenseForSlot(EquipmentSlot equipmentSlot) {
		return protectionAmounts[equipmentSlot.getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
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

	public static ArmorMaterial of(String id, int durability, int[] protection, int enchantability, SoundEvent soundEvent, float toughness, ItemLike repairItem) {
		return new SimpleArmorMaterial(id, durability, protection, enchantability, soundEvent, toughness, () -> Ingredient.of(repairItem.asItem()));
	}

	@Override
	public float getKnockbackResistance() {
		// TODO implement knock-back resistance
		return 0;
	}
}
