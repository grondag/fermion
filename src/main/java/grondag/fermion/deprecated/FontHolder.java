package grondag.fermion.deprecated;

import net.minecraft.util.Identifier;

@Deprecated
public class FontHolder
{
    public static String FONT_NAME_SMALL = "ubuntu-c.ttf";
    public static String FONT_NAME_LARGE = "ubuntu-m.ttf";
    public static int FONT_SIZE_SMALL = 512;
    public static int FONT_SIZE_LARGE = 512;
    public static Identifier FONT_RESOURCE_SMALL = RasterFont.getSpriteId(FONT_NAME_SMALL, FONT_SIZE_SMALL);
    public static Identifier FONT_RESOURCE_LARGE = RasterFont.getSpriteId(FONT_NAME_LARGE, FONT_SIZE_LARGE);
    public static RasterFont FONT_RENDERER_SMALL;
    public static RasterFont FONT_RENDERER_LARGE;
    
    //TODO: create hook to call this, or wait for events
    public static void preStitch()
    {
//        TextureMap map = event.getMap();
        FONT_RENDERER_SMALL = new RasterFont(FONT_NAME_SMALL, FONT_SIZE_SMALL, 2);
//        map.setTextureEntry(FONT_RENDERER_SMALL);
        FONT_RENDERER_LARGE = new RasterFont(FONT_NAME_LARGE, FONT_SIZE_LARGE, 2);
//        map.setTextureEntry(FONT_RENDERER_LARGE);
    }

    //TODO: create hook to call this, or wait for events
    public static void postStitch()
    {
        FontHolder.FONT_RENDERER_SMALL.postLoad();
        FontHolder.FONT_RENDERER_LARGE.postLoad();
    }

}
