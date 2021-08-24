package grondag.fermion.color;

import java.util.Locale;

import net.minecraft.client.resource.language.I18n;

/**
 * @deprecated  use ColorUtil
 */
@Deprecated
public enum Hue
{
	INFRARED,
	CHERRY,
	ROSE,
	POMEGRANATE,
	CRIMSON,
	SCARLET,
	RED,
	VERMILLION,
	TANGERINE,
	ORANGE,
	EMBER,
	SUNSET,
	PUMPKIN,
	CHEDDAR,
	MANGO,
	SUNFLOWER,
	GOLD,
	TORCH,
	YELLOW,
	LEMON,
	LIME,
	PERIDOT,
	CHARTREUSE,
	CACTUS,
	GREEN,
	FOLIAGE,
	MINT,
	SAGE,
	JUNIPER,
	CELADON,
	EMERALD,
	VERDIGRIS,
	TURQUOISE,
	SEA_FOAM,
	CYAN,
	ICE,
	BERYL,
	APATITE,
	MARINE,
	AQUA,
	ROBIN_EGG,
	MORNING,
	CERULEAN,
	TOPAZ,
	SKY,
	SAPPHIRE,
	PERIWINKLE,
	TWILIGHT,
	AZURE,
	OCEAN,
	COBALT,
	BLUE,
	LAPIS,
	INDIGO,
	VIOLET,
	PURPLE,
	AMETHYST,
	LILAC,
	MAGENTA,
	FUSCHIA,
	TULIP,
	PINK,
	PEONY;

	/**
	 * Rotate our color cylinder by this many degrees
	 * to tweak which colors we actually get.
	 * A purely aesthetic choice.
	 */
	private static final double HUE_SALT = 0;

	public static Hue[] VALUES = Hue.values();
	public static int COUNT = VALUES.length;

	private int hueSample = 0;

	public double hueDegrees()
	{
		return ordinal() * 360.0 / Hue.values().length + HUE_SALT;
	}

	/**
	 * Initialized lazily because ordinal not available during instantiation.
	 * @return
	 */
	public int hueSample()
	{
		if(hueSample == 0)
		{
			hueSample = Color.fromHCL(hueDegrees(), Color.HCL_MAX, Color.HCL_MAX).ARGB | 0xFF000000;
		}
		return hueSample;
	}

	public String localizedName()
	{
		return I18n.translate("color.hue." + name().toLowerCase(Locale.ROOT));
	}
}