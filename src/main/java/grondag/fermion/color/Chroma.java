package grondag.fermion.color;

import net.minecraft.client.resource.language.I18n;

public enum Chroma
{
    PURE_NETURAL(0),
    WHITE(2.5),
    GREY(5),
    NEUTRAL(10),
    RICH(20),
    DEEP(30),
    EXTRA_DEEP(40),
    BOLD(50),
    EXTRA_BOLD(60),
    ACCENT(70),
    INTENSE_ACCENT(80),
    ULTRA_ACCENT(90);

    public static final Chroma[] VALUES = Chroma.values();
    public static final int COUNT = VALUES.length;
    
    public final double value;

    private Chroma(double chromaValue)
    {
        this.value = chromaValue;
    }

    public String localizedName()
    {
        return I18n.translate("color.chroma." + this.name().toLowerCase());
    }
}