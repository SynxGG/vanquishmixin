package vanquishmixin;

import vanquishmixin.init.VanquishmixinModBlocks;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JeiPlugin
public final class VQJeiPlugin implements IModPlugin {

	private static final ResourceLocation PLUGIN_UID =
		new ResourceLocation(
			VanquishmixinMod.MODID,
			"jei_plugin"
		);

	/*
	 * Type JEI de la catégorie graphique.
	 *
	 * Il représente les mêmes objets Recipe que ceux
	 * utilisés par VQAdvancedCrafting.
	 */
	public static final RecipeType<
		VQWorkstationRecipes.WorkstationRecipe
	> WORKSTATION_RECIPE_TYPE =
		RecipeType.create(
			VanquishmixinMod.MODID,
			"workstation",
			VQWorkstationRecipes
				.WorkstationRecipe
				.class
		);

	public VQJeiPlugin() {
		System.out.println(
			"[VQ JEI] Plugin Vanquish détecté"
		);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_UID;
	}

	/*
	 * ============================================================
	 * CATÉGORIE
	 * ============================================================
	 */

	@Override
	public void registerCategories(
		IRecipeCategoryRegistration registration
	) {
		IGuiHelper guiHelper =
			registration
				.getJeiHelpers()
				.getGuiHelper();

		registration.addRecipeCategories(
			new WorkstationRecipeCategory(
				guiHelper
			)
		);

		System.out.println(
			"[VQ JEI] Catégorie Workstation enregistrée"
		);
	}

	/*
	 * ============================================================
	 * RECETTES
	 * ============================================================
	 */

	@Override
	public void registerRecipes(
		IRecipeRegistration registration
	) {
		ClientLevel level =
			Minecraft.getInstance().level;

		if (level == null) {
			System.err.println(
				"[VQ JEI] Impossible de lire les recettes : "
					+ "ClientLevel absent"
			);

			return;
		}

		List<
			VQWorkstationRecipes.WorkstationRecipe
		> recipes =
			new ArrayList<>(
				level
					.getRecipeManager()
					.getAllRecipesFor(
						VQWorkstationRecipes
							.WORKSTATION_TYPE
							.get()
					)
			);

		/*
		 * Ordre stable dans JEI.
		 */
		recipes.sort(
			Comparator.comparing(
				recipe ->
					recipe
						.getId()
						.toString()
			)
		);

		registration.addRecipes(
			WORKSTATION_RECIPE_TYPE,
			recipes
		);

		System.out.println(
			"[VQ JEI] "
				+ recipes.size()
				+ " recettes Workstation chargées depuis le RecipeManager"
		);
	}

	/*
	 * ============================================================
	 * CATALYSTS
	 * ============================================================
	 */

	@Override
	public void registerRecipeCatalysts(
		IRecipeCatalystRegistration registration
	) {
		registration.addRecipeCatalyst(
			VanquishmixinModBlocks
				.WORKSTATION_BLOCK
				.get(),
			WORKSTATION_RECIPE_TYPE
		);

		/*
		 * La Workstation reste aussi compatible avec les
		 * recettes Crafting vanilla, mod, KubeJS et datapack.
		 */
		registration.addRecipeCatalyst(
			VanquishmixinModBlocks
				.WORKSTATION_BLOCK
				.get(),
			RecipeTypes.CRAFTING
		);

		System.out.println(
			"[VQ JEI] Catalyst Workstation enregistré"
		);
	}

	/*
	 * ============================================================
	 * CATÉGORIE GRAPHIQUE
	 * ============================================================
	 */

	private static final class
	WorkstationRecipeCategory
	implements IRecipeCategory<
		VQWorkstationRecipes.WorkstationRecipe
	> {

		private static final int GRID_WIDTH = 3;
		private static final int GRID_HEIGHT = 3;
		private static final int GRID_SIZE = 9;

		private static final int WIDTH = 128;
		private static final int HEIGHT = 54;

		private final IDrawable icon;
		private final IDrawable recipeArrow;

		private WorkstationRecipeCategory(
			IGuiHelper guiHelper
		) {
			this.icon =
				guiHelper.createDrawableItemLike(
					VanquishmixinModBlocks
						.WORKSTATION_BLOCK
						.get()
				);

			this.recipeArrow =
				guiHelper.getRecipeArrow();
		}

		@Override
		public RecipeType<
			VQWorkstationRecipes.WorkstationRecipe
		> getRecipeType() {
			return WORKSTATION_RECIPE_TYPE;
		}

		@Override
		public Component getTitle() {
			return Component.literal(
				"Workstation"
			);
		}

		@Override
		public int getWidth() {
			return WIDTH;
		}

		@Override
		public int getHeight() {
			return HEIGHT;
		}

		@Override
		public IDrawable getIcon() {
			return icon;
		}

		@Override
		public void setRecipe(
			IRecipeLayoutBuilder builder,
			VQWorkstationRecipes.WorkstationRecipe recipe,
			IFocusGroup focuses
		) {
			if (
				recipe instanceof
				VQWorkstationRecipes
					.WorkstationShapedRecipe shapedRecipe
			) {
				addShapedInputs(
					builder,
					shapedRecipe
				);
			} else {
				addShapelessInputs(
					builder,
					recipe
				);
			}

			ItemStack output =
				recipe
					.getResultItem(
						getRegistryAccess()
					)
					.copy();

			builder
				.addOutputSlot(
					106,
					19
				)
				.setOutputSlotBackground()
				.addItemStack(output);
		}

		private static void addShapedInputs(
			IRecipeLayoutBuilder builder,
			VQWorkstationRecipes
				.WorkstationShapedRecipe recipe
		) {
			int recipeWidth =
				recipe.getWidth();

			int recipeHeight =
				recipe.getHeight();

			NonNullList<Ingredient> ingredients =
				recipe.getIngredients();

			for (
				int slotId = 0;
				slotId < GRID_SIZE;
				slotId++
			) {
				int column =
					slotId % GRID_WIDTH;

				int row =
					slotId / GRID_WIDTH;

				Ingredient ingredient =
					Ingredient.EMPTY;

				if (
					column < recipeWidth
					&& row < recipeHeight
				) {
					int ingredientIndex =
						row * recipeWidth
							+ column;

					if (
						ingredientIndex
							< ingredients.size()
					) {
						ingredient =
							ingredients.get(
								ingredientIndex
							);
					}
				}

				addInputSlot(
					builder,
					slotId,
					ingredient
				);
			}
		}

		private static void addShapelessInputs(
			IRecipeLayoutBuilder builder,
			VQWorkstationRecipes.WorkstationRecipe recipe
		) {
			builder.setShapeless();

			NonNullList<Ingredient> ingredients =
				recipe.getIngredients();

			for (
				int slotId = 0;
				slotId < GRID_SIZE;
				slotId++
			) {
				Ingredient ingredient =
					slotId < ingredients.size()
						? ingredients.get(slotId)
						: Ingredient.EMPTY;

				addInputSlot(
					builder,
					slotId,
					ingredient
				);
			}
		}

		private static void addInputSlot(
			IRecipeLayoutBuilder builder,
			int slotId,
			Ingredient ingredient
		) {
			int column =
				slotId % GRID_WIDTH;

			int row =
				slotId / GRID_WIDTH;

			IRecipeSlotBuilder slotBuilder =
				builder
					.addInputSlot(
						1 + column * 18,
						1 + row * 18
					)
					.setStandardSlotBackground();

			/*
			 * Un Ingredient peut représenter plusieurs items,
			 * par exemple un tag Forge.
			 *
			 * JEI les fera défiler dans le même slot.
			 */
			for (
				ItemStack possibleStack
					: ingredient.getItems()
			) {
				if (!possibleStack.isEmpty()) {
					slotBuilder.addItemStack(
						possibleStack
					);
				}
			}
		}

		private static RegistryAccess
		getRegistryAccess() {
			ClientLevel level =
				Minecraft.getInstance().level;

			return level != null
				? level.registryAccess()
				: RegistryAccess.EMPTY;
		}

		@Override
		public void draw(
			VQWorkstationRecipes.WorkstationRecipe recipe,
			IRecipeSlotsView recipeSlotsView,
			GuiGraphics guiGraphics,
			double mouseX,
			double mouseY
		) {
			recipeArrow.draw(
				guiGraphics,
				67,
				20
			);
		}

		@Override
		public ResourceLocation getRegistryName(
			VQWorkstationRecipes.WorkstationRecipe recipe
		) {
			return recipe.getId();
		}
	}
}