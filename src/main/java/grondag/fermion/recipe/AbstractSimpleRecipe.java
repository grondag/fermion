package grondag.fermion.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public abstract class AbstractSimpleRecipe implements SimpleRecipe<Inventory> {
	public final Ingredient ingredient;
	public final ItemStack result;
	public final Identifier id;
	public final String group;
	public final int cost;

	public AbstractSimpleRecipe(Identifier id, String group, Ingredient ingredient, int cost, ItemStack result) {
		this.id = id;
		this.group = group;
		this.ingredient = ingredient;
		this.result = result;
		this.cost = cost;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public boolean matches(Inventory inventory, World world) {
		return ingredient.test(inventory.getStack(0));
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
	public ItemStack getOutput() {
		return result;
	}

	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		final DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(ingredient);
		return defaultedList;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height) {
		return true;
	}

	@Override
	public ItemStack craft(Inventory inventory) {
		return result.copy();
	}

	@FunctionalInterface
	public interface Factory<T extends AbstractSimpleRecipe> {
		T create(Identifier id, String group, Ingredient ingredient, int cost, ItemStack result);
	}
}