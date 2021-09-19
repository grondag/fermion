package grondag.fermion.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public abstract class AbstractSimpleRecipe implements SimpleRecipe<Container> {
	public final Ingredient ingredient;
	public final ItemStack result;
	public final ResourceLocation id;
	public final String group;
	public final int cost;

	public AbstractSimpleRecipe(ResourceLocation id, String group, Ingredient ingredient, int cost, ItemStack result) {
		this.id = id;
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
		this.cost = cost;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public boolean matches(Container inventory, Level world) {
		return ingredient.test(inventory.getItem(0));
	}

	@Override
	public boolean matches(ItemStack stack) {
		return ingredient.test(stack);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public String getGroup() {
		return group;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		final NonNullList<Ingredient> defaultedList = NonNullList.create();
		defaultedList.add(ingredient);
		return defaultedList;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public ItemStack assemble(Container inventory) {
		return result.copy();
	}

	@FunctionalInterface
	public interface Factory<T extends AbstractSimpleRecipe> {
		T create(ResourceLocation id, String group, Ingredient ingredient, int cost, ItemStack result);
	}
}