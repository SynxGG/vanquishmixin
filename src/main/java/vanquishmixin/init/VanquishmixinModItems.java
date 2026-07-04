/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package vanquishmixin.init;

import vanquishmixin.item.IchorItem;

import vanquishmixin.VanquishmixinMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class VanquishmixinModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, VanquishmixinMod.MODID);
	public static final RegistryObject<Item> WORKSTATION_BLOCK;
	public static final RegistryObject<Item> ICHOR;
	static {
		WORKSTATION_BLOCK = block(VanquishmixinModBlocks.WORKSTATION_BLOCK, new Item.Properties().fireResistant());
		ICHOR = REGISTRY.register("ichor", IchorItem::new);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return block(block, new Item.Properties());
	}

	private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}