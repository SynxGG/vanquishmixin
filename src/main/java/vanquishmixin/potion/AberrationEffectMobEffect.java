package vanquishmixin.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import net.minecraftforge.registries.ForgeRegistries;

/**
 * Generic composite effect.
 *
 * This class does not know where Aberration comes from. It can be granted by
 * a command, a potion, KubeJS, an armor set, a mob, or any future system.
 *
 * While Aberration is active, it refreshes the real component effects:
 * - Majrusz Bleeding Immunity I
 * - Hunger I
 * - Unluck I
 * - Quark Resilience II
 * - Resistance II
 */
public class AberrationEffectMobEffect extends MobEffect {

    private static final int COMPONENT_DURATION_TICKS = 20;
    private static final int REFRESH_INTERVAL_TICKS = 10;

    private static final ResourceLocation BLEEDING_IMMUNITY =
        new ResourceLocation("majruszsdifficulty", "bleeding_immunity");
    private static final ResourceLocation HUNGER =
        new ResourceLocation("minecraft", "hunger");
    private static final ResourceLocation UNLUCK =
        new ResourceLocation("minecraft", "unluck");
    private static final ResourceLocation RESILIENCE =
        new ResourceLocation("quark", "resilience");
    private static final ResourceLocation RESISTANCE =
        new ResourceLocation("minecraft", "resistance");

    public AberrationEffectMobEffect() {
        super(MobEffectCategory.HARMFUL, -13434829);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // applyEffectTick is called every tick. The method itself limits the
        // refresh to once every 10 entity ticks.
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return;
        }

        if (entity.tickCount % REFRESH_INTERVAL_TICKS != 0) {
            return;
        }

        applyComponent(entity, BLEEDING_IMMUNITY, 0);
        applyComponent(entity, HUNGER, 0);
        applyComponent(entity, UNLUCK, 0);
        applyComponent(entity, RESILIENCE, 1);
        applyComponent(entity, RESISTANCE, 1);
    }

    private static void applyComponent(
        LivingEntity entity,
        ResourceLocation effectId,
        int amplifier
    ) {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);

        if (effect == null) {
            return;
        }

        entity.addEffect(
            new MobEffectInstance(
                effect,
                COMPONENT_DURATION_TICKS,
                amplifier,
                false,
                false,
                false
            )
        );
    }
}
