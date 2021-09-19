package grondag.fermion.entity;

import net.minecraft.world.effect.MobEffectInstance;

public interface StatusEffectAccess {
	void fermion_setDuration(int duration);

	void fermion_setAmplifier(int amplifier);

	void fermion_addDuration(int duration);

	default void fermion_set(int duration, int amplifier) {
		fermion_setDuration(duration);
		fermion_setAmplifier(amplifier);
	}

	static StatusEffectAccess access(MobEffectInstance instance) {
		return (StatusEffectAccess) instance;
	}
}
