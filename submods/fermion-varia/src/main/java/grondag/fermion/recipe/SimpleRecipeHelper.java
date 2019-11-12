package grondag.fermion.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

public class SimpleRecipeHelper implements SimpleSynchronousResourceReloadListener {
	private final ImmutableSet<RecipeType<?>> recipeTypes;

	private MinecraftServer server;

	private final IdentityHashMap<RecipeType<?>, ArrayList<SimpleRecipe<?>>> recipes = new IdentityHashMap<>();

	private final Collection<Identifier> RELOAD_DEPS = Collections.singletonList(ResourceReloadListenerKeys.RECIPES);

	private final Identifier id;

	public SimpleRecipeHelper(Identifier id, RecipeType<?>... recipeTypes) {
		this.id = id;
		this.recipeTypes = ImmutableSet.copyOf(recipeTypes);
	}

	@Override
	public Identifier getFabricId() {
		return id;
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		recipes.clear();
		reload();
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return RELOAD_DEPS;
	}

	private void reload() {
		if (server != null) {
			final RecipeManager rm = server.getRecipeManager();

			for (final Recipe<?> r : rm.values()) {
				if (recipeTypes.contains(r.getType())) {
					recipes.computeIfAbsent(r.getType(), k -> new ArrayList<SimpleRecipe<?>>()).add((SimpleRecipe<?>) r);
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
