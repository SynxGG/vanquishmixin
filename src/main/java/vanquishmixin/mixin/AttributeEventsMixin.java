package vanquishmixin.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces AttributesLib's integer-truncating XP handlers with an equivalent
 * implementation that preserves fractional XP between events.
 *
 * Without this patch, a 5% or 10% bonus is often rounded down to zero on mobs
 * worth five XP. The stored remainder makes the long-term reward exact while
 * retaining the normal integer XP orb output.
 */
@Pseudo
@Mixin(
    targets = "dev.shadowsoffire.attributeslib.impl.AttributeEvents",
    remap = false
)
public abstract class AttributeEventsMixin {

    private static final ResourceLocation EXPERIENCE_GAINED_ID =
        new ResourceLocation("attributeslib", "experience_gained");

    private static final String XP_REMAINDER_KEY =
        "vanquish.attributeslib_xp_remainder";

    @Inject(
        method = "blockBreak",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void vanquish$preciseBlockExperience(
        BlockEvent.BreakEvent event,
        CallbackInfo callback
    ) {
        Player player = event.getPlayer();
        event.setExpToDrop(
            vanquish$applyPreciseMultiplier(player, event.getExpToDrop())
        );
        callback.cancel();
    }

    @Inject(
        method = "mobXp",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void vanquish$preciseMobExperience(
        LivingExperienceDropEvent event,
        CallbackInfo callback
    ) {
        Player player = event.getAttackingPlayer();
        if (player != null) {
            event.setDroppedExperience(
                vanquish$applyPreciseMultiplier(
                    player,
                    event.getDroppedExperience()
                )
            );
        }

        // The original AttributesLib handler does nothing without an attacking
        // player, so canceling is behaviorally identical in that case too.
        callback.cancel();
    }

    private static int vanquish$applyPreciseMultiplier(
        Player player,
        int baseExperience
    ) {
        if (player == null || baseExperience <= 0) {
            return Math.max(0, baseExperience);
        }

        Attribute experienceAttribute =
            ForgeRegistries.ATTRIBUTES.getValue(EXPERIENCE_GAINED_ID);

        if (experienceAttribute == null) {
            return baseExperience;
        }

        double multiplier = player.getAttributeValue(experienceAttribute);
        if (!Double.isFinite(multiplier) || multiplier < 0.0D) {
            return baseExperience;
        }

        CompoundTag persistentData = player.getPersistentData();
        double storedRemainder = persistentData.getDouble(XP_REMAINDER_KEY);
        double exactExperience =
            baseExperience * multiplier + storedRemainder;

        if (exactExperience >= Integer.MAX_VALUE) {
            persistentData.putDouble(XP_REMAINDER_KEY, 0.0D);
            return Integer.MAX_VALUE;
        }

        int wholeExperience = (int) Math.floor(exactExperience);
        persistentData.putDouble(
            XP_REMAINDER_KEY,
            exactExperience - wholeExperience
        );

        return wholeExperience;
    }
}
