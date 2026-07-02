package vanquishmixin.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class AberrationEffectMobEffect extends MobEffect {
	public AberrationEffectMobEffect() {
		super(MobEffectCategory.HARMFUL, -13434829);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}