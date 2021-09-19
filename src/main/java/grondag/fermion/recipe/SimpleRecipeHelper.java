package grondag.fermion.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;

import com.google.common.collect.ImmutableSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class SimpleRecipeHelper implements SimpleSynchronousResourceReloadListener {
	private final ImmutableSet<RecipeType<?>> recipeTypes;

	private MinecraftServer server;

	private final IdentityHashMap<RecipeType<?>, ArrayList<SimpleRecipe<?>>> recipes = new IdentityHashMap<>();

	private final Collection<ResourceLocation> RELOAD_DEPS = Collections.singletonList(ResourceReloadListenerKeys.RECIPES);

	private final ResourceLocation id;

	public SimpleRecipeHelper(ResourceLocation id, RecipeType<?>... recipeTypes) {
		this.id = id;
		this.recipeTypes = ImmutableSet.copyOf(recipeTypes);
	}

	@Override
	public ResourceLocation getFabricId() {
		return id;
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		recipes.clear();
		reload();
	}

	@Override
	public Collection<ResourceLocation> getFabricDependencies() {
		return RELOAD_DEPS;
	}

	private void reload() {
		if (server != null) {
			final RecipeManager rm = server.getRecipeManager();

			for (final Recipe<?> r : rm.getRecipes()) {
				if (recipeTypes.contains(r.getType())) {
					recipes.computeIfAbsent(r.getType(), k -> new ArrayList<>()).add((SimpleRecipe<?>) r);
				}
			}
		}
	}

	public void init(MinecraftServer serverIn) {
		server = serverIn;
		reload();
	}

	public void stop(MinecraftServer serverIn) {
		server = null;
	}

	@SuppressWarnings("unchecked")
	public <T extends SimpleRecipe<?>> T get(RecipeType<?> recipeType, ItemStack stack) {
		if (recipes != null) {
			final ArrayList<SimpleRecipe<?>> list = recipes.get(recipeType);
			if (list != null && !list.isEmpty()) {
				for (final SimpleRecipe<?> r : list) {
					if (r.matches(stack))
						return (T) r;
				}
			}
		}

		return null;
	}
}
