package grondag.fermion.recipe;

import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import grondag.fermion.recipe.AbstractSimpleRecipe.Factory;

public class SimpleRecipeSerializer<T extends AbstractSimpleRecipe> implements RecipeSerializer<T> {
	protected final Factory<T> factory;

	public SimpleRecipeSerializer(Factory<T> factory) {
		this.factory = factory;
	}

	@Override
	public T fromJson(ResourceLocation identifier, JsonObject jsonObject) {
		final String group = GsonHelper.getAsString(jsonObject, "group", "");
		Ingredient ingredient;
		if (GsonHelper.isArrayNode(jsonObject, "ingredient")) {
			ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "ingredient"));
		} else {
			ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "ingredient"));
		}

		final String result = GsonHelper.getAsString(jsonObject, "result");
		final ItemStack itemStack = new ItemStack(Registry.ITEM.get(new ResourceLocation(result)), 1);
		final int cost = GsonHelper.getAsInt(jsonObject, "cost", 0);
		return factory.create(identifier, group, ingredient, cost, itemStack);
	}

	@Override
	public T fromNetwork(ResourceLocation identifier, FriendlyByteBuf packetByteBuf) {
		final String group = packetByteBuf.readUtf(32767);
		final Ingredient ingredient = Ingredient.fromNetwork(packetByteBuf);
		final ItemStack result = packetByteBuf.readItem();
		final int cost = packetByteBuf.readVarInt();
		return factory.create(identifier, group, ingredient, cost, result);
	}

	@Override
	public void toNetwork(FriendlyByteBuf packetByteBuf, AbstractSimpleRecipe recipe) {
		packetByteBuf.writeUtf(recipe.group);
		recipe.ingredient.toNetwork(packetByteBuf);
		packetByteBuf.writeItem(recipe.result);
		packetByteBuf.writeVarInt(recipe.cost);
	}
}
