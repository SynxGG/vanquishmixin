/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package vanquishmixin.init;

import vanquishmixin.block.WorkstationBlockBlock;

import vanquishmixin.VanquishmixinMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

public class VanquishmixinModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, VanquishmixinMod.MODID);
	public static final RegistryObject<Block> WORKSTATION_BLOCK;
	static {
		WORKSTATION_BLOCK = REGISTRY.register("workstation_block", WorkstationBlockBlock::new);
	}
	// Start of user code block custom blocks
	// End of user code block custom blocks
}