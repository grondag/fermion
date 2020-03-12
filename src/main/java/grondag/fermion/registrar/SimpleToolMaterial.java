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

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

public class SimpleToolMaterial implements ToolMaterial {
	public final int miningLevel;
	public final int itemDurability;
	public final float miningSpeed;
	public final float attackDamage;
	public final int enchantability;
	public final Lazy<Ingredient> repairIngredient;

	public SimpleToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> ingredientSupplier) {
		this.miningLevel = miningLevel;
		this.itemDurability = itemDurability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		repairIngredient = new Lazy<>(ingredientSupplier);
	}

	@Override
	public int getDurability() {
		return itemDurability;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return miningSpeed;
	}

	@Override
	public float getAttackDamage() {
		return attackDamage;
	}

	@Override
	public int getMiningLevel() {
		return miningLevel;
	}

	@Override
	public int getEnchantability() {
		return enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return repairIngredient.get();
	}

	public static ToolMaterial of(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Supplier<Ingredient> ingredientSupplier) {
		return new SimpleToolMaterial(miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, ingredientSupplier);
	}

	public static ToolMaterial of(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, ItemConvertible repairItem) {
		return new SimpleToolMaterial(miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, () -> Ingredient.ofItems(repairItem.asItem()));
	}
}
