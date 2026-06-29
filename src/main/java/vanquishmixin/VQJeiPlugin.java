package vanquishmixin;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;

import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class VQJeiPlugin implements IModPlugin {

	private static final ResourceLocation PLUGIN_UID =
		new ResourceLocation(
			VanquishmixinMod.MODID,
			"jei_plugin"
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
}