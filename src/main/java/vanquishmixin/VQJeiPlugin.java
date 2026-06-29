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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public final class VQJeiPlugin implements IModPlugin {

	private static final ResourceLocation PLUGIN_UID =
		new ResourceLocation(
			VanquishmixinMod.MODID,
			"jei_plugin"
		);

	private static final String ANCIENT_METAL =
		"cataclysm:ancient_metal_ingot";

	/*
	 * Type de recette personnalisé reconnu par JEI.
	 */
	public static final RecipeType<WorkstationJeiRecipe>
		WORKSTATION_RECIPE_TYPE =
			RecipeType.create(
				VanquishmixinMod.MODID,
				"workstation",
				WorkstationJeiRecipe.class
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
	 * CATÉGORIE JEI
	 * ============================================================
	 */

	@Override
	public void registerCategories(
		IRecipeCategoryRegistration registration
	) {
		System.out.println(
			"[VQ JEI] Enregistrement catégorie Workstation"
		);

		IGuiHelper guiHelper =
			registration
				.getJeiHelpers()
				.getGuiHelper();

		registration.addRecipeCategories(
			new WorkstationRecipeCategory(guiHelper)
		);
	}

	/*
	 * ============================================================
	 * RECETTES EXCLUSIVES
	 * ============================================================
	 */

	@Override
	public void registerRecipes(
		IRecipeRegistration registration
	) {
		List<WorkstationJeiRecipe> recipes =
			createRecipes();

		System.out.println(
			"[VQ JEI] Enregistrement de "
				+ recipes.size()
				+ " recettes Workstation"
		);

		registration.addRecipes(
			WORKSTATION_RECIPE_TYPE,
			recipes
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
		System.out.println(
			"[VQ JEI] Enregistrement catalyst Workstation"
		);

		/*
		 * La Workstation ouvre sa catégorie exclusive.
		 */
		registration.addRecipeCatalyst(
			VanquishmixinModBlocks
				.WORKSTATION_BLOCK
				.get(),
			WORKSTATION_RECIPE_TYPE
		);

		/*
		 * Elle est également capable d'utiliser toutes
		 * les recettes Crafting standard.
		 */
		registration.addRecipeCatalyst(
			VanquishmixinModBlocks
				.WORKSTATION_BLOCK
				.get(),
			RecipeTypes.CRAFTING
		);
	}

	/*
	 * ============================================================
	 * DÉFINITION DES RECETTES JEI
	 * ============================================================
	 *
	 * Elles sont volontairement définies ici pour le moment.
	 * Cette classe ne modifie absolument pas le moteur de craft.
	 */

	private static List<WorkstationJeiRecipe>
	createRecipes() {
		List<WorkstationJeiRecipe> recipes =
			new ArrayList<>();

		/*
		 * Warrior Helmet
		 *
		 * A A A
		 * A . A
		 * . . .
		 */
		addRecipe(
			recipes,
			"warrior_helmet",
			grid(
				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				null,
				null,
				null
			),
			"immersive_armors:warrior_helmet"
		);

		/*
		 * Warrior Chestplate
		 *
		 * A . A
		 * A A A
		 * A A A
		 */
		addRecipe(
			recipes,
			"warrior_chestplate",
			grid(
				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL
			),
			"immersive_armors:warrior_chestplate"
		);

		/*
		 * Warrior Leggings
		 *
		 * A A A
		 * A . A
		 * A . A
		 */
		addRecipe(
			recipes,
			"warrior_leggings",
			grid(
				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL
			),
			"immersive_armors:warrior_leggings"
		);

		/*
		 * Warrior Boots
		 *
		 * A . A
		 * A . A
		 * . . .
		 */
		addRecipe(
			recipes,
			"warrior_boots",
			grid(
				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				null,
				null,
				null
			),
			"immersive_armors:warrior_boots"
		);

		return List.copyOf(recipes);
	}

	private static void addRecipe(
		List<WorkstationJeiRecipe> recipes,
		String recipeName,
		ItemStack[] inputs,
		String outputId
	) {
		ItemStack output =
			resolveStack(outputId);

		if (output.isEmpty()) {
			System.err.println(
				"[VQ JEI] Recette ignorée, output introuvable : "
					+ outputId
			);

			return;
		}

		ResourceLocation recipeId =
			new ResourceLocation(
				VanquishmixinMod.MODID,
				"workstation/"
					+ recipeName
			);

		recipes.add(
			new WorkstationJeiRecipe(
				recipeId,
				inputs,
				output
			)
		);
	}

	private static ItemStack[] grid(
		String... itemIds
	) {
		if (itemIds.length != 9) {
			throw new IllegalArgumentException(
				"Une recette Workstation doit contenir exactement 9 cases."
			);
		}

		ItemStack[] inputs =
			new ItemStack[9];

		for (
			int slotId = 0;
			slotId < 9;
			slotId++
		) {
			inputs[slotId] =
				resolveStack(itemIds[slotId]);
		}

		return inputs;
	}

	private static ItemStack resolveStack(
		String itemId
	) {
		if (
			itemId == null
			|| itemId.isBlank()
		) {
			return ItemStack.EMPTY;
		}

		ResourceLocation location =
			ResourceLocation.tryParse(itemId);

		if (location == null) {
			System.err.println(
				"[VQ JEI] ResourceLocation invalide : "
					+ itemId
			);

			return ItemStack.EMPTY;
		}

		Item item =
			BuiltInRegistries.ITEM
				.getOptional(location)
				.orElse(Items.AIR);

		if (item == Items.AIR) {
			System.err.println(
				"[VQ JEI] Item introuvable : "
					+ itemId
			);

			return ItemStack.EMPTY;
		}

		return new ItemStack(item);
	}

	/*
	 * ============================================================
	 * MODÈLE INTERNE D'UNE RECETTE JEI
	 * ============================================================
	 */

	private record WorkstationJeiRecipe(
		ResourceLocation id,
		ItemStack[] inputs,
		ItemStack output
	) {
	}

	/*
	 * ============================================================
	 * CATÉGORIE GRAPHIQUE JEI
	 * ============================================================
	 */

	private static final class
	WorkstationRecipeCategory
	implements IRecipeCategory<WorkstationJeiRecipe> {

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
		public RecipeType<WorkstationJeiRecipe>
		getRecipeType() {
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
			WorkstationJeiRecipe recipe,
			IFocusGroup focuses
		) {
			ItemStack[] inputs =
				recipe.inputs();

			/*
			 * Grille 3×3.
			 */
			for (
				int slotId = 0;
				slotId < 9;
				slotId++
			) {
				int column =
					slotId % 3;

				int row =
					slotId / 3;

				int slotX =
					1 + column * 18;

				int slotY =
					1 + row * 18;

				IRecipeSlotBuilder slotBuilder =
					builder
						.addInputSlot(
							slotX,
							slotY
						)
						.setStandardSlotBackground();

				ItemStack ingredient =
					inputs[slotId];

				if (!ingredient.isEmpty()) {
					slotBuilder.addItemStack(
						ingredient
					);
				}
			}

			/*
			 * Résultat avec le grand cadre vanilla.
			 */
			builder
				.addOutputSlot(
					106,
					19
				)
				.setOutputSlotBackground()
				.addItemStack(
					recipe.output()
				);
		}

		@Override
		public void draw(
			WorkstationJeiRecipe recipe,
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
			WorkstationJeiRecipe recipe
		) {
			return recipe.id();
		}
	}
}