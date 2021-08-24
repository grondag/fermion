package grondag.fermion.color;

import java.util.Locale;

import net.minecraft.client.resource.language.I18n;

/**
 * @deprecated  use ColorUtil
 */
@Deprecated
public enum Luminance
{
	BRILLIANT(90),
	EXTRA_BRIGHT(81),
	BRIGHT(72),
	EXTRA_LIGHT(63),
	LIGHT(54),
	MEDIUM_LIGHT(45),
	MEDIUM_DARK(36),
	DARK(27),
	EXTRA_DARK(13);

	public static final Luminance[] VALUES = Luminance.values();
	public static final int COUNT = VALUES.length;

	public final double value;

	Luminance(double luminanceValue)
	{
		value = luminanceValue;
	}

	public String localizedName()
	{
		return I18n.translate("color.luminance." + name().toLowerCase(Locale.ROOT));
	}
}