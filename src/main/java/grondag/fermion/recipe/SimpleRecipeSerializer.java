package grondag.fermion.recipe;

import com.google.gson.JsonObject;

import grondag.fermion.recipe.AbstractSimpleRecipe.Factory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SimpleRecipeSerializer<T extends AbstractSimpleRecipe> implements RecipeSerializer<T> {
	protected final Factory<T> factory;

	public SimpleRecipeSerializer(Factory<T> factory) {
		this.factory = factory;
	}

	@Override
	public T read(Identifier identifier, JsonObject jsonObject) {
		final String group = JsonHelper.getString(jsonObject, "group", "");
		Ingredient ingredient;
		if (JsonHelper.hasArray(jsonObject, "ingredient")) {
			ingredient = Ingredient.fromJson(JsonHelper.getArray(jsonObject, "ingredient"));
		} else {
			ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
		}

		final String result = JsonHelper.getString(jsonObject, "result");
		final ItemStack itemStack = new ItemStack(Registry.ITEM.get(new Identifier(result)), 1);
		final int cost = JsonHelper.getInt(jsonObject, "cost", 0);
		return factory.create(identifier, group, ingredient, cost, itemStack);
	}

	@Override
	public T read(Identifier identifier, PacketByteBuf packetByteBuf) {
		final String group = packetByteBuf.readString(32767);
		final Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
		final ItemStack result = packetByteBuf.readItemStack();
		final int cost = packetByteBuf.readVarInt();
		return factory.create(identifier, group, ingredient, cost, result);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf, AbstractSimpleRecipe recipe) {
		packetByteBuf.writeString(recipe.group);
		recipe.ingredient.write(packetByteBuf);
		packetByteBuf.writeItemStack(recipe.result);
		packetByteBuf.writeVarInt(recipe.cost);
	}
}
