package vanquishmixin.mixin.client;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.client.ForgeHooksClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes Forge's inventory effect list respect MobEffectInstance#showIcon().
 *
 * Aberration creates its component effects with showIcon=false, so they keep
 * all of their real gameplay behavior while disappearing from the inventory
 * effect list. Normal visible instances of Hunger, Resistance, etc. are not
 * affected.
 */
@Mixin(value = ForgeHooksClient.class, remap = false)
public abstract class ForgeHooksClientMixin {

    @Inject(
        method = "shouldRenderEffect",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void vanquishmixin$hideHiddenEffectInstances(
        MobEffectInstance effectInstance,
        CallbackInfoReturnable<Boolean> callback
    ) {
        if (!effectInstance.showIcon()) {
            callback.setReturnValue(false);
        }
    }
}
