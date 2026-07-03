package vanquishmixin;

import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Infrastructure data-driven des recettes Workstation.
 *
 * Un seul RecipeType :
 *   vanquishmixin:workstation
 *
 * Deux serializers JSON :
 *   vanquishmixin:workstation_shaped
 *   vanquishmixin:workstation_shapeless
 */
public final class VQWorkstationRecipes {

	private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
		DeferredRegister.create(
			Registries.RECIPE_TYPE,
			VanquishmixinMod.MODID
		);

	private static final DeferredRegister<RecipeSerializer<?>>
		RECIPE_SERIALIZERS =
			DeferredRegister.create(
				Registries.RECIPE_SERIALIZER,
				VanquishmixinMod.MODID
			);

	public static final RegistryObject<RecipeType<WorkstationRecipe>>
		WORKSTATION_TYPE =
			RECIPE_TYPES.register(
				"workstation",
				VQWorkstationRecipes::createWorkstationType
			);

	public static final RegistryObject<
		RecipeSerializer<WorkstationShapedRecipe>
	> WORKSTATION_SHAPED =
		RECIPE_SERIALIZERS.register(
			"workstation_shaped",
			WorkstationShapedSerializer::new
		);

	public static final RegistryObject<
		RecipeSerializer<WorkstationShapelessRecipe>
	> WORKSTATION_SHAPELESS =
		RECIPE_SERIALIZERS.register(
			"workstation_shapeless",
			WorkstationShapelessSerializer::new
		);

	private VQWorkstationRecipes() {
	}

	public static void register(IEventBus bus) {
		RECIPE_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}

	private static RecipeType<WorkstationRecipe>
	createWorkstationType() {
		return new RecipeType<>() {
			@Override
			public String toString() {
				return VanquishmixinMod.MODID
					+ ":workstation";
			}
		};
	}

	/**
	 * Marqueur commun aux recettes shaped et shapeless.
	 */
	public interface WorkstationRecipe
	extends CraftingRecipe {
	}

	/**
	 * Recette shaped Workstation.
	 *
	 * Elle réutilise tout le matching vanilla :
	 * déplacement dans la grille, miroir horizontal,
	 * tags, ingrédients Forge, etc.
	 */
	public static final class WorkstationShapedRecipe
	extends ShapedRecipe
	implements WorkstationRecipe {

		public WorkstationShapedRecipe(
			ResourceLocation id,
			String group,
			CraftingBookCategory category,
			int width,
			int height,
			NonNullList<Ingredient> ingredients,
			ItemStack result
		) {
			super(
				id,
				group,
				category,
				width,
				height,
				ingredients,
				result
			);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return WORKSTATION_SHAPED.get();
		}

		@Override
		public RecipeType<?> getType() {
			return WORKSTATION_TYPE.get();
		}
	}

	/**
	 * Recette shapeless Workstation.
	 */
	public static final class WorkstationShapelessRecipe
	extends ShapelessRecipe
	implements WorkstationRecipe {

		public WorkstationShapelessRecipe(
			ResourceLocation id,
			String group,
			CraftingBookCategory category,
			ItemStack result,
			NonNullList<Ingredient> ingredients
		) {
			super(
				id,
				group,
				category,
				result,
				ingredients
			);
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return WORKSTATION_SHAPELESS.get();
		}

		@Override
		public RecipeType<?> getType() {
			return WORKSTATION_TYPE.get();
		}
	}

	/**
	 * Le serializer custom délègue la lecture à Minecraft.
	 *
	 * Cela évite de réécrire nous-mêmes le parser shaped
	 * et conserve la compatibilité avec les ingrédients Forge.
	 */
	private static final class WorkstationShapedSerializer
	implements RecipeSerializer<WorkstationShapedRecipe> {

		@Override
		public WorkstationShapedRecipe fromJson(
			ResourceLocation recipeId,
			JsonObject json
		) {
			ShapedRecipe vanillaRecipe =
				RecipeSerializer.SHAPED_RECIPE
					.fromJson(recipeId, json);

			return wrapShaped(vanillaRecipe);
		}

		@Override
		public WorkstationShapedRecipe fromNetwork(
			ResourceLocation recipeId,
			FriendlyByteBuf buffer
		) {
			ShapedRecipe vanillaRecipe =
				RecipeSerializer.SHAPED_RECIPE
					.fromNetwork(recipeId, buffer);

			return wrapShaped(vanillaRecipe);
		}

		@Override
		public void toNetwork(
			FriendlyByteBuf buffer,
			WorkstationShapedRecipe recipe
		) {
			RecipeSerializer.SHAPED_RECIPE
				.toNetwork(buffer, recipe);
		}
	}

	private static final class WorkstationShapelessSerializer
	implements RecipeSerializer<WorkstationShapelessRecipe> {

		@Override
		public WorkstationShapelessRecipe fromJson(
			ResourceLocation recipeId,
			JsonObject json
		) {
			ShapelessRecipe vanillaRecipe =
				RecipeSerializer.SHAPELESS_RECIPE
					.fromJson(recipeId, json);

			return wrapShapeless(vanillaRecipe);
		}

		@Override
		public WorkstationShapelessRecipe fromNetwork(
			ResourceLocation recipeId,
			FriendlyByteBuf buffer
		) {
			ShapelessRecipe vanillaRecipe =
				RecipeSerializer.SHAPELESS_RECIPE
					.fromNetwork(recipeId, buffer);

			return wrapShapeless(vanillaRecipe);
		}

		@Override
		public void toNetwork(
			FriendlyByteBuf buffer,
			WorkstationShapelessRecipe recipe
		) {
			RecipeSerializer.SHAPELESS_RECIPE
				.toNetwork(buffer, recipe);
		}
	}

	private static WorkstationShapedRecipe wrapShaped(
		ShapedRecipe recipe
	) {
		return new WorkstationShapedRecipe(
			recipe.getId(),
			recipe.getGroup(),
			recipe.category(),
			recipe.getWidth(),
			recipe.getHeight(),
			copyIngredients(recipe.getIngredients()),
			recipe
				.getResultItem(RegistryAccess.EMPTY)
				.copy()
		);
	}

	private static WorkstationShapelessRecipe wrapShapeless(
		ShapelessRecipe recipe
	) {
		return new WorkstationShapelessRecipe(
			recipe.getId(),
			recipe.getGroup(),
			recipe.category(),
			recipe
				.getResultItem(RegistryAccess.EMPTY)
				.copy(),
			copyIngredients(recipe.getIngredients())
		);
	}

	private static NonNullList<Ingredient> copyIngredients(
		NonNullList<Ingredient> source
	) {
		NonNullList<Ingredient> copy =
			NonNullList.create();

		copy.addAll(source);

		return copy;
	}
}