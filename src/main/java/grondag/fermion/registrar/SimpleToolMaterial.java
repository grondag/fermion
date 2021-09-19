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
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class SimpleToolMaterial implements Tier {
	public final int miningLevel;
	public final int itemDurability;
	public final float miningSpeed;
	public final float attackDamage;
	public final int enchantability;
	public final LazyLoadedValue<Ingredient> repairIngredient;

	public SimpleToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> ingredientSupplier) {
		this.miningLevel = miningLevel;
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		repairIngredient = new LazyLoadedValue<>(ingredientSupplier);
	}

	@Override
	public int getUses() {
		return itemDurability;
	}

	@Override
	public float getSpeed() {
		return miningSpeed;
	}

	@Override
	public float getAttackDamageBonus() {
		return attackDamage;
	}

	@Override
	public int getLevel() {
		return miningLevel;
	}

	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repairIngredient.get();
	}

	public static Tier of(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> ingredientSupplier) {
		return new SimpleToolMaterial(miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, ingredientSupplier);
	}

	public static Tier of(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, ItemLike repairItem) {
		return new SimpleToolMaterial(miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, () -> Ingredient.of(repairItem.asItem()));
	}
}
