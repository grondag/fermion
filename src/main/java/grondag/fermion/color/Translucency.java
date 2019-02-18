package grondag.fermion.color;

/**
 * TODO: Remove after translucency picker is redone.
 */
@Deprecated
public enum Translucency
{
    CLEAR(0.1F),
    TINTED(0.4F),
    SHADED(0.6F),
    STAINED(0.8F);
    
    public final float alpha;
    public final int alphaARGB;
    /** used by block method */
    public final int blockLightOpacity;
    
    private Translucency(float alpha)
    {
        this.alpha = alpha;
        this.blockLightOpacity = (int) Math.round((alpha - 0.2F) * 15);
        this.alphaARGB = ((int)Math.round(alpha * 255) << 24);
    }
}
