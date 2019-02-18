package grondag.fermion.font;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FontHolder
{
    public static String FONT_NAME_SMALL = "ubuntu-c.ttf";
    public static String FONT_NAME_LARGE = "ubuntu-m.ttf";
    public static int FONT_SIZE_SMALL = 512;
    public static int FONT_SIZE_LARGE = 512;
    public static String FONT_RESOURCE_STRING_SMALL = RasterFont.getSpriteResourceName(FONT_NAME_SMALL, FONT_SIZE_SMALL);
    public static String FONT_RESOURCE_STRING_LARGE = RasterFont.getSpriteResourceName(FONT_NAME_LARGE, FONT_SIZE_LARGE);
    @SuppressWarnings("null")
    public static RasterFont FONT_RENDERER_SMALL;
    @SuppressWarnings("null")
    public static RasterFont FONT_RENDERER_LARGE;
    
    public static void preStitch(TextureStitchEvent.Pre event)
    {
        TextureMap map = event.getMap();
        FONT_RENDERER_SMALL = new RasterFont(FONT_NAME_SMALL, FONT_SIZE_SMALL, 2);
        map.setTextureEntry(FONT_RENDERER_SMALL);
        FONT_RENDERER_LARGE = new RasterFont(FONT_NAME_LARGE, FONT_SIZE_LARGE, 2);
        map.setTextureEntry(FONT_RENDERER_LARGE);
        
    }

    public static void postStitch(TextureStitchEvent.Post event)
    {
        FontHolder.FONT_RENDERER_SMALL.postLoad();
        FontHolder.FONT_RENDERER_LARGE.postLoad();
    }

}
