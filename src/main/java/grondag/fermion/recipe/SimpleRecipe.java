package grondag.fermion.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public interface SimpleRecipe<C extends Inventory> extends Recipe<C> {
	boolean matches(ItemStack stack);
}
