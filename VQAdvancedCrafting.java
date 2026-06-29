package vanquishmixin;

import vanquishmixin.world.inventory.WorkstationMenu;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public final class VQAdvancedCrafting {

	/*
	 * IDs custom du GUI :
	 *
	 * 0 1 2
	 * 3 4 5
	 * 6 7 8
	 *
	 * Résultat : 9
	 */
	private static final int GRID_WIDTH = 3;
	private static final int GRID_HEIGHT = 3;
	private static final int GRID_SIZE = 9;
	private static final int OUTPUT_SLOT = 9;

	private static final String ANCIENT_METAL =
		"cataclysm:ancient_metal_ingot";

	/*
	 * Évite les appels récursifs.
	 *
	 * Retirer un ingrédient déclenche setChanged(),
	 * qui tente normalement de recalculer la recette.
	 */
	private static final Set<WorkstationMenu> BUSY_MENUS =
		Collections.newSetFromMap(new WeakHashMap<>());

	/*
	 * ============================================================
	 * RECETTES EXCLUSIVES VANQUISH
	 * ============================================================
	 *
	 * Elles sont testées AVANT les recettes de crafting normales.
	 */
	private static final SpecialRecipe[] SPECIAL_RECIPES = {

		/*
		 * Warrior Helmet
		 *
		 * A A A
		 * A . A
		 * . . .
		 */
		new SpecialRecipe(
			new String[] {
				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				null,
				null,
				null
			},
			new int[] {
				1, 1, 1,
				1, 0, 1,
				0, 0, 0
			},
			"immersive_armors:warrior_helmet",
			1
		),

		/*
		 * Warrior Helmet décalé vers le bas
		 *
		 * . . .
		 * A A A
		 * A . A
		 */
		new SpecialRecipe(
			new String[] {
				null,
				null,
				null,

				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL
			},
			new int[] {
				0, 0, 0,
				1, 1, 1,
				1, 0, 1
			},
			"immersive_armors:warrior_helmet",
			1
		),

		/*
		 * Warrior Chestplate
		 *
		 * A . A
		 * A A A
		 * A A A
		 */
		new SpecialRecipe(
			new String[] {
				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL
			},
			new int[] {
				1, 0, 1,
				1, 1, 1,
				1, 1, 1
			},
			"immersive_armors:warrior_chestplate",
			1
		),

		/*
		 * Warrior Leggings
		 *
		 * A A A
		 * A . A
		 * A . A
		 */
		new SpecialRecipe(
			new String[] {
				ANCIENT_METAL,
				ANCIENT_METAL,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL
			},
			new int[] {
				1, 1, 1,
				1, 0, 1,
				1, 0, 1
			},
			"immersive_armors:warrior_leggings",
			1
		),

		/*
		 * Warrior Boots
		 *
		 * A . A
		 * A . A
		 * . . .
		 */
		new SpecialRecipe(
			new String[] {
				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				null,
				null,
				null
			},
			new int[] {
				1, 0, 1,
				1, 0, 1,
				0, 0, 0
			},
			"immersive_armors:warrior_boots",
			1
		),

		/*
		 * Warrior Boots décalées vers le bas
		 *
		 * . . .
		 * A . A
		 * A . A
		 */
		new SpecialRecipe(
			new String[] {
				null,
				null,
				null,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL,

				ANCIENT_METAL,
				null,
				ANCIENT_METAL
			},
			new int[] {
				0, 0, 0,
				1, 0, 1,
				1, 0, 1
			},
			"immersive_armors:warrior_boots",
			1
		)
	};

	private VQAdvancedCrafting() {
	}

	/*
	 * ============================================================
	 * COMPATIBILITÉ AVEC LES PROCÉDURES MCREATOR
	 * ============================================================
	 */

	public static void updateOutput(Entity entity) {
		if (
			entity instanceof ServerPlayer player
			&& player.containerMenu instanceof WorkstationMenu menu
		) {
			updateOutput(menu);
		}
	}

	public static void takeOutput(Entity entity) {
		if (
			entity instanceof ServerPlayer player
			&& player.containerMenu instanceof WorkstationMenu menu
		) {
			takeOutput(menu);
		}
	}

	/*
	 * ============================================================
	 * API APPELÉE DIRECTEMENT PAR WORKSTATIONMENU
	 * ============================================================
	 */

	/**
	 * Recalcule le résultat sans consommer les ingrédients.
	 */
	public static void updateOutput(WorkstationMenu menu) {
		if (!isServerMenu(menu)) {
			return;
		}

		if (!BUSY_MENUS.add(menu)) {
			return;
		}

		try {
			refreshOutput(menu);
		} finally {
			BUSY_MENUS.remove(menu);
		}
	}

	/**
	 * Consomme les ingrédients lorsque le joueur récupère
	 * le résultat du slot 9.
	 */
	public static void takeOutput(WorkstationMenu menu) {
		if (!isServerMenu(menu)) {
			return;
		}

		if (!BUSY_MENUS.add(menu)) {
			return;
		}

		try {
			/*
			 * Priorité absolue aux recettes spéciales Vanquish.
			 */
			SpecialRecipe specialRecipe =
				findSpecialRecipe(menu);

			if (specialRecipe != null) {
				consumeSpecialIngredients(
					menu,
					specialRecipe
				);

				refreshOutput(menu);
				menu.broadcastChanges();
				return;
			}

			/*
			 * Sinon, recherche d'une recette standard :
			 *
			 * - Vanilla
			 * - Mods
			 * - KubeJS
			 * - Datapacks
			 */
			TransientCraftingContainer craftingGrid =
				createCraftingGrid(menu);

			Optional<CraftingRecipe> standardRecipe =
				findStandardRecipe(
					menu,
					craftingGrid
				);

			if (standardRecipe.isEmpty()) {
				clearOutputInternal(menu);
				menu.broadcastChanges();
				return;
			}

			consumeStandardIngredients(
				menu,
				craftingGrid,
				standardRecipe.get()
			);

			refreshOutput(menu);
			menu.broadcastChanges();

		} finally {
			BUSY_MENUS.remove(menu);
		}
	}

	/**
	 * Supprime le résultat virtuel avant la fermeture du GUI.
	 *
	 * Cela évite que MCreator rende gratuitement l'objet
	 * du slot 9 au joueur.
	 */
	public static void clearOutput(WorkstationMenu menu) {
		if (!isServerMenu(menu)) {
			return;
		}

		if (!BUSY_MENUS.add(menu)) {
			return;
		}

		try {
			clearOutputInternal(menu);
			menu.broadcastChanges();
		} finally {
			BUSY_MENUS.remove(menu);
		}
	}

	/*
	 * ============================================================
	 * CALCUL DU RÉSULTAT
	 * ============================================================
	 */

	private static void refreshOutput(WorkstationMenu menu) {
		ItemStack expectedOutput =
			findExpectedOutput(menu);

		Slot outputSlot =
			getCustomSlot(menu, OUTPUT_SLOT);

		if (outputSlot == null) {
			return;
		}

		ItemStack currentOutput =
			outputSlot.getItem();

		if (
			stacksAreExactlyEqual(
				currentOutput,
				expectedOutput
			)
		) {
			return;
		}

		if (expectedOutput.isEmpty()) {
			outputSlot.set(ItemStack.EMPTY);
		} else {
			outputSlot.set(expectedOutput.copy());
		}

		outputSlot.setChanged();
		menu.broadcastChanges();
	}

	private static ItemStack findExpectedOutput(
		WorkstationMenu menu
	) {
		/*
		 * 1. Recettes exclusives Workstation.
		 */
		SpecialRecipe specialRecipe =
			findSpecialRecipe(menu);

		if (specialRecipe != null) {
			return createSpecialResult(
				specialRecipe
			);
		}

		/*
		 * 2. Toutes les recettes normales 3×3.
		 */
		TransientCraftingContainer craftingGrid =
			createCraftingGrid(menu);

		Optional<CraftingRecipe> standardRecipe =
			findStandardRecipe(
				menu,
				craftingGrid
			);

		if (standardRecipe.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack result =
			standardRecipe.get().assemble(
				craftingGrid,
				menu.world.registryAccess()
			);

		if (result == null || result.isEmpty()) {
			return ItemStack.EMPTY;
		}

		return result.copy();
	}

	/*
	 * ============================================================
	 * RECETTES STANDARD VANILLA / MODS / KUBEJS
	 * ============================================================
	 */

	private static TransientCraftingContainer createCraftingGrid(
		WorkstationMenu menu
	) {
		NonNullList<ItemStack> items =
			NonNullList.withSize(
				GRID_SIZE,
				ItemStack.EMPTY
			);

		for (
			int slotId = 0;
			slotId < GRID_SIZE;
			slotId++
		) {
			Slot slot =
				getCustomSlot(menu, slotId);

			if (
				slot != null
				&& !slot.getItem().isEmpty()
			) {
				items.set(
					slotId,
					slot.getItem().copy()
				);
			}
		}

		return new TransientCraftingContainer(
			menu,
			GRID_WIDTH,
			GRID_HEIGHT,
			items
		);
	}

	private static Optional<CraftingRecipe> findStandardRecipe(
		WorkstationMenu menu,
		TransientCraftingContainer craftingGrid
	) {
		return menu.world
			.getRecipeManager()
			.getRecipeFor(
				RecipeType.CRAFTING,
				craftingGrid,
				menu.world
			);
	}

	private static void consumeStandardIngredients(
		WorkstationMenu menu,
		TransientCraftingContainer craftingGrid,
		CraftingRecipe recipe
	) {
		/*
		 * Calculé AVANT de retirer les ingrédients.
		 *
		 * Exemples :
		 * - seau vide ;
		 * - bouteille vide ;
		 * - outil restant ;
		 * - contenant spécifique d'un mod.
		 */
		NonNullList<ItemStack> remainingItems =
			recipe.getRemainingItems(
				craftingGrid
			);

		for (
			int slotId = 0;
			slotId < GRID_SIZE;
			slotId++
		) {
			Slot inputSlot =
				getCustomSlot(menu, slotId);

			if (inputSlot == null) {
				continue;
			}

			ItemStack originalStack =
				craftingGrid.getItem(slotId);

			/*
			 * Une recette de crafting standard consomme
			 * une unité de chaque slot utilisé.
			 */
			if (!originalStack.isEmpty()) {
				inputSlot.remove(1);
				inputSlot.setChanged();
			}

			ItemStack remainingStack =
				slotId < remainingItems.size()
					? remainingItems.get(slotId)
					: ItemStack.EMPTY;

			if (!remainingStack.isEmpty()) {
				insertRemainingItem(
					menu.entity,
					inputSlot,
					remainingStack.copy()
				);
			}
		}
	}

	private static void insertRemainingItem(
		Player player,
		Slot inputSlot,
		ItemStack remainingStack
	) {
		if (remainingStack.isEmpty()) {
			return;
		}

		ItemStack currentStack =
			inputSlot.getItem();

		/*
		 * Le slot est vide :
		 * le contenant retourne directement dans la grille.
		 */
		if (currentStack.isEmpty()) {
			inputSlot.set(remainingStack);
			inputSlot.setChanged();
			return;
		}

		/*
		 * Le contenant peut rejoindre le stack déjà présent.
		 */
		if (
			ItemStack.isSameItemSameTags(
				currentStack,
				remainingStack
			)
		) {
			int maximumStackSize =
				Math.min(
					currentStack.getMaxStackSize(),
					inputSlot.getMaxStackSize()
				);

			int combinedCount =
				currentStack.getCount()
				+ remainingStack.getCount();

			if (combinedCount <= maximumStackSize) {
				currentStack.grow(
					remainingStack.getCount()
				);

				inputSlot.setChanged();
				return;
			}
		}

		/*
		 * Sinon :
		 * inventaire du joueur, puis drop au sol.
		 */
		ItemStack itemToGive =
			remainingStack.copy();

		boolean inserted =
			player.getInventory().add(itemToGive);

		if (!inserted && !itemToGive.isEmpty()) {
			player.drop(itemToGive, false);
		}
	}

	/*
	 * ============================================================
	 * RECETTES EXCLUSIVES VANQUISH
	 * ============================================================
	 */

	private static SpecialRecipe findSpecialRecipe(
		WorkstationMenu menu
	) {
		for (
			SpecialRecipe recipe : SPECIAL_RECIPES
		) {
			if (matchesSpecialRecipe(menu, recipe)) {
				return recipe;
			}
		}

		return null;
	}

	private static boolean matchesSpecialRecipe(
		WorkstationMenu menu,
		SpecialRecipe recipe
	) {
		for (
			int slotId = 0;
			slotId < GRID_SIZE;
			slotId++
		) {
			Slot slot =
				getCustomSlot(menu, slotId);

			if (slot == null) {
				return false;
			}

			ItemStack stack =
				slot.getItem();

			String requiredItemId =
				recipe.inputIds()[slotId];

			int requiredCount =
				recipe.inputCounts()[slotId];

			/*
			 * Un emplacement absent dans la recette
			 * doit rester entièrement vide.
			 */
			if (
				requiredCount <= 0
				|| requiredItemId == null
				|| requiredItemId.isBlank()
			) {
				if (!stack.isEmpty()) {
					return false;
				}

				continue;
			}

			Item expectedItem =
				resolveItem(requiredItemId);

			if (expectedItem == Items.AIR) {
				return false;
			}

			if (!stack.is(expectedItem)) {
				return false;
			}

			if (stack.getCount() < requiredCount) {
				return false;
			}
		}

		return true;
	}

	private static ItemStack createSpecialResult(
		SpecialRecipe recipe
	) {
		Item outputItem =
			resolveItem(recipe.outputId());

		if (
			outputItem == Items.AIR
			|| recipe.outputCount() <= 0
		) {
			return ItemStack.EMPTY;
		}

		return new ItemStack(
			outputItem,
			recipe.outputCount()
		);
	}

	private static void consumeSpecialIngredients(
		WorkstationMenu menu,
		SpecialRecipe recipe
	) {
		for (
			int slotId = 0;
			slotId < GRID_SIZE;
			slotId++
		) {
			int amount =
				recipe.inputCounts()[slotId];

			if (amount <= 0) {
				continue;
			}

			Slot inputSlot =
				getCustomSlot(menu, slotId);

			if (inputSlot == null) {
				continue;
			}

			inputSlot.remove(amount);
			inputSlot.setChanged();
		}
	}

	/*
	 * ============================================================
	 * UTILITAIRES
	 * ============================================================
	 */

	private static boolean isServerMenu(
		WorkstationMenu menu
	) {
		return menu != null
			&& menu.world != null
			&& !menu.world.isClientSide();
	}

	/**
	 * Utilise l'ID custom MCreator.
	 *
	 * Ne pas remplacer par menu.getSlot(slotId), car MCreator
	 * n'ajoute pas nécessairement les slots dans l'ordre visuel.
	 */
	private static Slot getCustomSlot(
		WorkstationMenu menu,
		int slotId
	) {
		return menu.getSlots().get(slotId);
	}

	private static void clearOutputInternal(
		WorkstationMenu menu
	) {
		Slot outputSlot =
			getCustomSlot(menu, OUTPUT_SLOT);

		if (
			outputSlot == null
			|| outputSlot.getItem().isEmpty()
		) {
			return;
		}

		outputSlot.set(ItemStack.EMPTY);
		outputSlot.setChanged();
	}

	private static boolean stacksAreExactlyEqual(
		ItemStack first,
		ItemStack second
	) {
		if (
			first.isEmpty()
			&& second.isEmpty()
		) {
			return true;
		}

		if (
			first.isEmpty()
			|| second.isEmpty()
		) {
			return false;
		}

		if (
			first.getCount()
			!= second.getCount()
		) {
			return false;
		}

		return ItemStack.isSameItemSameTags(
			first,
			second
		);
	}

	private static Item resolveItem(
		String itemId
	) {
		if (
			itemId == null
			|| itemId.isBlank()
		) {
			return Items.AIR;
		}

		ResourceLocation location =
			ResourceLocation.tryParse(itemId);

		if (location == null) {
			return Items.AIR;
		}

		return BuiltInRegistries.ITEM
			.getOptional(location)
			.orElse(Items.AIR);
	}

	private record SpecialRecipe(
		String[] inputIds,
		int[] inputCounts,
		String outputId,
		int outputCount
	) {
	}
}