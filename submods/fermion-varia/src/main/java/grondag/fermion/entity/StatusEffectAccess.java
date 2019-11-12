package grondag.fermion.entity;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface StatusEffectAccess {
	void fermion_setDuration(int duration);

	void fermion_setAmplifier(int amplifier);

	void fermion_addDuration(int duration);

	default void fermion_set(int duration, int amplifier) {
		fermion_setDuration(duration);
		fermion_setAmplifier(amplifier);
	}

	static StatusEffectAccess access(StatusEffectInstance instance) {
		return (StatusEffectAccess) instance;
	}
}
