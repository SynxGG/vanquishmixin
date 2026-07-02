/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package vanquishmixin.init;

import vanquishmixin.VanquishmixinMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

public class VanquishmixinModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, VanquishmixinMod.MODID);
	public static final RegistryObject<MobEffect> ABERRATION_EFFECT = REGISTRY.register("aberration_effect", AberrationEffectMobEffect::new);
}