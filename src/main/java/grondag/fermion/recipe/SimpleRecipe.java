package grondag.fermion.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public interface SimpleRecipe<C extends Container> extends Recipe<C> {
	boolean matches(ItemStack stack);
}
