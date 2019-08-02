package grondag.fermion.deprecated;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.sun.imageio.plugins.png.PNGImageWriter;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;

import grondag.fermion.Fermion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

/**
 * Static version of TrueTypeFont that uses the block texture map.
 * Enables basic text rendering on blocks and items.
 */

@Deprecated
@SuppressWarnings("restriction")
public class RasterFont extends Sprite
{
    
    /**
     * Characters we support for in-world rendering. Limited to reduce texture map consumption.
     */
    private static final String CHARSET = "+-%.=?!/0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final char[] SUBSCRIPTS = {0x2080, 0x2081, 0x2082, 0x2083, 0x2084, 0x2085, 0x2086, 0x2087, 0x2088, 0x2089};
    
    /**
     * Pixels around each glyph in texture map to prevent bleeding
     */
    private static int GLYPH_MARGIN = 2;
    private static int GLYPH_MARGIN_X2 = GLYPH_MARGIN * 2;
    
    public static Identifier getSpriteId(String fontName, int fontSize)
    {
        return new Identifier("fermion", "blocks/" + fontName + "-" + fontSize);
    }
    
    /**
     * Array that holds necessary information about the font characters
     */
    private GlyphInfo[] glyphArray = new GlyphInfo[266];

    /** determined by start character - assumes all the same */
    public final int fontHeight;
    
    /** 
     * Adjust horizontal spacing if font needs it.
     */
    public final int horizontalSpacing;
    
    /**
     * Pixel width to use for monospace rendering of numbers.
     */ 
    public final int monoWidth;
    
    
    /**
     * Created during init, loaded to texture map during load, null after that.
     */
    @SuppressWarnings("unused")
    private BufferedImage fontMap;


    /**
     * Font name should include the extension.
     */
    @SuppressWarnings("unused")
    public RasterFont(String fontName, final int textureSize, int horizontalSpacing)
    {
        super(getSpriteId(fontName, textureSize), textureSize, textureSize);
        this.horizontalSpacing = horizontalSpacing;
        
        // initial guess - shoot for too big
        int fontSize = textureSize / 8;
        int monoWidth = 0;
        int fontHeight = 0;
        boolean isSizeRight = false;
        sizeloop:
        while(!isSizeRight)
        {
            Font font = getFont(new Identifier("fermion", "fonts/" + fontName), fontSize);
            
            // Create a temporary image to extract the character's size
            
            final BufferedImage sizeImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D sizeGraphics = (Graphics2D) sizeImage.getGraphics();
            sizeGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            sizeGraphics.setFont(font);
            final FontMetrics fontMetrics = sizeGraphics.getFontMetrics();
            fontHeight = fontMetrics.getHeight();

            // compute width for monospacing numeric characters
            int maxWidth = 0;
            for(char c : CHARSET.toCharArray())
            {
                if(Character.isDigit(c))
                {
                    int cWidth = fontMetrics.charWidth(c);
                    if(cWidth > maxWidth) maxWidth = cWidth;
                }
            }
            monoWidth = maxWidth;

            try
            {
                final BufferedImage glyphMapImage = new BufferedImage(textureSize, textureSize, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D glyphGraphics = (Graphics2D) glyphMapImage.getGraphics();
    
                glyphGraphics.setColor(new Color(0, 0, 0, 1));
                glyphGraphics.fillRect(0, 0, textureSize, textureSize);
    
                int positionX = GLYPH_MARGIN;
                int positionY = GLYPH_MARGIN;
    
                for (char ch : CHARSET.toCharArray())
                {
                    BufferedImage fontImage = getFontImage(ch, fontHeight, font, fontMetrics);
    
                    if (positionX + fontImage.getWidth() >= textureSize)
                    {
                        positionX = GLYPH_MARGIN;
                        positionY += fontHeight + GLYPH_MARGIN_X2;
                        
                        if(positionY + fontHeight + GLYPH_MARGIN_X2 > textureSize)
                        {
                            // if overrun then retry with larger size
                            fontSize--;
                            continue sizeloop;
                        }
                    }
                    
                    GlyphInfo glyph = new GlyphInfo(fontImage.getWidth(), fontHeight, positionX, positionY, Character.isDigit(ch) ? monoWidth : 0);
    
                    // Draw it here
                    glyphGraphics.drawImage(fontImage, positionX, positionY, null);
    
                    positionX += fontImage.getWidth() + GLYPH_MARGIN_X2;
                    
                    glyphArray[ch] = glyph;
    
                    fontImage = null;
                }
    
                int i = 256;
                for (char ch : SUBSCRIPTS)
                {
                    BufferedImage fontImage = getFontImage(ch, fontHeight, font, fontMetrics);
    
                    if (positionX + fontImage.getWidth() >= textureSize)
                    {
                        positionX = GLYPH_MARGIN;
                        positionY += fontHeight + GLYPH_MARGIN_X2;
                        
                        if(positionY + fontHeight + GLYPH_MARGIN_X2 > textureSize)
                        {
                            // if overrun then retry with larger size
                            fontSize--;
                            continue sizeloop;
                        }
                    }
                    
                    GlyphInfo glyph = new GlyphInfo(fontImage.getWidth(), fontHeight, positionX, positionY, Character.isDigit(ch) ? monoWidth : 0);
    
                    // Draw it here
                    glyphGraphics.drawImage(fontImage, positionX, positionY, null);
    
                    positionX += fontImage.getWidth() + GLYPH_MARGIN_X2;
                    
                    glyphArray[i++] = glyph;
    
                    fontImage = null;
                }
                
                // if we got to here, then size is correct
                isSizeRight = true;
                
                this.fontMap = glyphMapImage;
                
                //output font textures for troubleshooting - quick hack, not pretty
                if(false) { //FermionConfig.RENDER.outputFontTexturesForDebugging)
                    File file = new File(fontName + "-" + fontHeight + ".png");
                    if(file.exists()) file.delete();
                    file.createNewFile();
                    FileImageOutputStream output = new FileImageOutputStream(file);
                    PNGImageWriterSpi spiPNG = new PNGImageWriterSpi();
                    PNGImageWriter writer = (PNGImageWriter) spiPNG.createWriterInstance();
                    writer.setOutput(output);
                    writer.write(glyphMapImage);
                    output.close();
                }
            }
            catch (Exception e)
            {
                Fermion.LOG.error("Failed to create font. Stuff won't render correctly if it renders at all.", e);
            }
        }
        this.fontHeight = fontHeight;
        this.monoWidth = monoWidth;
    }
    
    private BufferedImage getFontImage(char ch, int fontHeight, Font font, FontMetrics fontMetrics)
    {
    
        // Create another image holding the character we are creating
        BufferedImage fontImage;
        fontImage = new BufferedImage(fontMetrics.charWidth(ch), fontHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gt = (Graphics2D) fontImage.getGraphics();
        gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gt.setFont(font);

        gt.setColor(Color.WHITE);
        gt.drawString(String.valueOf(ch), 0, fontMetrics.getAscent());

        return fontImage;
    }

    private Font getFont(Identifier res, float size)
    {
        Font font;
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, MinecraftClient.getInstance().getResourceManager().getResource(res).getInputStream());
            return font.deriveFont(size);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /** must be called after texture map creation to set interpolated values */
    public void postLoad()
    {
        for(GlyphInfo g : this.glyphArray)
        {
            if(g != null) g.updateInterpolated();
        }
    }
    
    public GlyphInfo getGlyphInfo(char c)
    {
        if(c < 0 || c > 255) return null;
        return this.glyphArray[c];
    }
    
    public GlyphInfo getNumericSubscript(int i)
    {
        if(i < 0 || i > 9) return null;
        return this.glyphArray[256 + i];
    }
    public int getWidth(String text)
    {
        int result = 0;
        for(char c : text.toCharArray())
        {
            if(this.glyphArray[c] != null) result += this.glyphArray[c].renderWidth;
        }
        return result;
    }
    
    /**
     * Digits are given equal widths. Otherwise same as {@link #getWidth(String)}
     */
    public int getWidthMonospaced(String text)
    {
        int result = 0;
        for(char c : text.toCharArray())
        {
            if(this.glyphArray[c] != null) result += this.glyphArray[c].monoOffset > 0 ? this.monoWidth : this.glyphArray[c].renderWidth;
        }
        return result;
    }

    /**
     * Digits are given half their normal width. Otherwise same as {@link #getWidth(String)}
     */
    public int getWidthFormula(String text)
    {
        float result = 0;
        for(char c : text.toCharArray())
        {
            if(this.glyphArray[c] != null) result += Character.isDigit(c) ? this.getNumericSubscript(Character.getNumericValue(c)).renderWidth : this.glyphArray[c].renderWidth;
        }
        return (int) result;
    }


    /**
     * Draws a single line of text at the given x, y coordinate, with z depth
     * Rendering will start at x and y and extend right and down.
     * GL matrix should be set to that +y is in the down direction for the viewer.
     */
    public void drawLine(double xLeft, double yTop, String text, double lineHeight, double zDepth, int red, int green, int blue, int alpha)
    {
        // moved this to machine rendering setup
        GlStateManager.bindTexture(MinecraftClient.getInstance().getSpriteAtlas().getGlId());
        
        // moved this to machine rendering setup
//        TextureHelper.setTextureBlurMipmap(true, true);
        
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        BufferBuilder buffer = Tessellator.getInstance().getBufferBuilder();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);

        double x = xLeft;
        double scaleFactor = lineHeight / this.fontHeight;
        
        for(char c : text.toCharArray())
        {
            GlyphInfo g = this.glyphArray[c];
            if(g != null)
            {
            bufferQuad(buffer, x, yTop, scaleFactor, g, red, green, blue, alpha);
            x += (g.renderWidth) * scaleFactor;
            }
        }
        Tessellator.getInstance().draw();

    }
    
    /**
     * Like {@link #drawLine(double, double, String, double, double, int, int, int, int)}
     * but renders all numbers as subscripts.
     */
    public void drawFormula(double xLeft, double yTop, String text, double lineHeight, double zDepth, int red, int green, int blue, int alpha)
    {
        GlStateManager.bindTexture(MinecraftClient.getInstance().getSpriteAtlas().getGlId());
        TextureHelper.setTextureBlurMipmap(true, true);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        BufferBuilder buffer = Tessellator.getInstance().getBufferBuilder();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);

        double x = xLeft;
        double scaleFactor = lineHeight / this.fontHeight;
        
        for(char c : text.toCharArray())
        {
            GlyphInfo g = Character.isDigit(c) ? this.getNumericSubscript(Character.getNumericValue(c)) : this.glyphArray[c];
            if(g != null)
            {
            bufferQuad(buffer, x, yTop, scaleFactor, g, red, green, blue, alpha);
            x += (g.renderWidth) * scaleFactor;
            }
        }
        Tessellator.getInstance().draw();

    }
    /**
     * Just like {@link #drawLine(double, double, String, double, double, int, int, int, int)} but
     * with monospace character spacing for numbers. 
     */
    public void drawLineMonospaced(double xLeft, double yTop, String text, double lineHeight, double zDepth, int red, int green, int blue, int alpha)
    {
        GlStateManager.bindTexture(MinecraftClient.getInstance().getSpriteAtlas().getGlId());
        TextureHelper.setTextureBlurMipmap(true, true);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        BufferBuilder buffer = Tessellator.getInstance().getBufferBuilder();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);

        double x = xLeft;
        double scaleFactor = lineHeight / this.fontHeight;
        
        for(char c : text.toCharArray())
        {
            GlyphInfo g = this.glyphArray[c];
            if(g != null)
            {
            bufferQuad(buffer, x + g.monoOffset * scaleFactor, yTop, scaleFactor, g, red, green, blue, alpha);
            x += (g.monoOffset > 0 ? this.monoWidth : g.pixelWidth) * scaleFactor;
            }
        }
        Tessellator.getInstance().draw();

    }
    
    private void bufferQuad(BufferBuilder buffer, double xLeft, double yTop, double scaleFactor, GlyphInfo glyph, int red, int green, int blue, int alpha)
    {
        double xRight = xLeft + glyph.pixelWidth * scaleFactor;
        double yBottom = yTop + glyph.glyphHeight * scaleFactor;

        buffer.vertex(xLeft, yTop, 0).color(red, green, blue, alpha).texture(glyph.interpolatedMinU, glyph.interpolatedMinV);
        buffer.vertex(xLeft, yBottom, 0).color(red, green, blue, alpha).texture(glyph.interpolatedMinU, glyph.interpolatedMaxV);
        buffer.vertex(xRight, yBottom, 0).color(red, green, blue, alpha).texture(glyph.interpolatedMaxU, glyph.interpolatedMaxV);
        buffer.vertex(xRight, yTop, 0).color(red, green, blue, alpha).texture(glyph.interpolatedMaxU, glyph.interpolatedMinV);
        // FIX?
        // not sure if this will really work - no longer have a per-vertex lightmap method
        // if not, may need to pre-pack and use purVertexData
        buffer.brightness(0x00f000f0, 0x00f000f0, 0x00f000f0, 0x00f000f0);
    }
    
    //TODO: refactor to produce Fabric Render API meshes
    /**
     * Generates quads to render the given text on all faces of a block.  
     * The quads are oriented to be readable and are positioned in the top half of the block.
     * Assumes the quads will be rendered on a typical 1x1 square block face. 
     * 
     * 
     * @param text
     * @param formatAsForumla If true then all numbers will be rendered as subscript. 
     * @param color
     * @param bumpFactor  Use to scale face out to prevent z-fighting. Should be >= 1f.
     * @param leftSide If false will be centered.
     * @param list
     */
//    public void formulaBlockQuadsToList(String text, boolean formatAsForumla, int color, float bumpFactor, boolean leftSide, List<IPolygon> list)
//    {
//        IMutablePolygon template = PolyFactory.COMMON_POOL.newPaintable(4);
//        template.setTextureName(0, FontHolder.FONT_RESOURCE_SMALL);
//        template.setLockUV(0, false);
//        template.setShouldContractUVs(0, false);
//        
//        int pixelWidth = formatAsForumla ? this.getWidthFormula(text) : this.getWidth(text);
//        
//        // try fitting to height start
//        float height =  0.5f;
//        float width = height * pixelWidth / this.fontHeight;
//        
//        if(width > 0.98f)
//        {
//            // too wide, so justify to width instead
//            width = 0.98f;
//            height = width * this.fontHeight / pixelWidth;
//        }
//                
//        float scaleFactor = height / this.fontHeight;
//        float left = (1 - width) / (leftSide ? 4 : 2);
//
//        for(char c : text.toCharArray())
//        {
//            boolean isSubscript = formatAsForumla && Character.isDigit(c);
//            GlyphInfo g = isSubscript ? this.getNumericSubscript(Character.getNumericValue(c)) : this.getGlyphInfo(c);
//            if(g != null)
//            {
//                float glyphWidth = g.pixelWidth * scaleFactor;
//                FaceVertex[] fv;
//                fv = makeFaceVertices(g, left, 1.05f, glyphWidth, height, color);
//                left += glyphWidth;
//                
//                for(Direction face : Direction.VALUES)
//                {
//                    template.setupFaceQuad(face, fv[0], fv[1], fv[2], fv[3], null);
//                    template.scaleFromBlockCenter(bumpFactor);
//                    list.add(template.toPainted());
//                }
//                
//            }
//        }
//        template.release();
//    }
//    
//    private FaceVertex[] makeFaceVertices(GlyphInfo glyph, float left, float top, float width, float height, int color)
//    {
//        float bottom = top - height;
//        float right = left + width;
//        
//        FaceVertex[] result = new FaceVertex[4];
//        
//        result[0] = new FaceVertex.UVColored(left, bottom, 0, glyph.uMinNormal, glyph.vMaxNormal, color, 0);
//        result[1] = new FaceVertex.UVColored(right, bottom, 0, glyph.uMaxNormal, glyph.vMaxNormal, color, 0);
//        result[2] = new FaceVertex.UVColored(right, top, 0, glyph.uMaxNormal, glyph.vMinNormal, color, 0);
//        result[3] = new FaceVertex.UVColored(left, top, 0, glyph.uMinNormal, glyph.vMinNormal, color, 0);
//        
//        return result;
//    }
 
    //FIX: figure out how/where this happens now
//    @Override
//    public boolean load(ResourceManager manager, ResourceLocation location, Function<ResourceLocation, Sprite> textureGetter)
//    {
//        BufferedImage bufferedimage = this.fontMap;
//        this.fontMap = null;
//        int mipmapLevels = MinecraftClient.getMinecraft().gameSettings.mipmapLevels;
//        
//        int[][] aint = new int[mipmapLevels + 1][];
//        aint[0] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
//        bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[0], 0, bufferedimage.getWidth());
//        this.framesTextureData.add(aint);
//        this.generateMipmaps(mipmapLevels);
//        
//        // Comments on super appear to be incorrect.
//        // False causes us to be included in map,
//        // which is what we want.
//        return false;
//    }

    @Override
    public void load(Resource resource, int mipmaplevels) throws IOException
    {
        //noop - all done in other
    }
    
    public class GlyphInfo
    {
        /**
         * Character's pixelWidth
         */
        public final int pixelWidth;
        
        /**
         * Includes the padding constant.
         */
        public final int renderWidth;

        /**
         * Character's height
         */
        public final int glyphHeight;

        
        /**
         * Character's x location within texture
         */
        public final int positionX;

        /**
         * Character's y position within texture
         */
        public final int positionY;
        
        /** texture coordinate relative to sprite, scaled 0-16 */
        public final float uMinMinecraft;
        /** texture coordinate relative to sprite, scaled 0-16 */
        public final float vMinMinecraft;
        /** texture coordinate relative to sprite, scaled 0-16 */
        public final float uMaxMinecraft;
        /** texture coordinate relative to sprite, scaled 0-16 */
        public final float vMaxMinecraft;
        
        /** texture coordinate relative to sprite, scaled 0-1 */
        public final float uMinNormal;
        /** texture coordinate relative to sprite, scaled 0-1 */
        public final float vMinNormal;
        /** texture coordinate relative to sprite, scaled 0-1 */
        public final float uMaxNormal;
        /** texture coordinate relative to sprite, scaled 0-1 */
        public final float vMaxNormal;

        /** texture coordinate within OpenGL texture, scaled 0-1  */
        public float interpolatedMinU;
        /** texture coordinate within OpenGL texture, scaled 0-1  */
        public float interpolatedMinV;
        /** texture coordinate within OpenGL texture, scaled 0-1  */
        public float interpolatedMaxU;
        /** texture coordinate within OpenGL texture, scaled 0-1  */
        public float interpolatedMaxV;
        
        /** 
         * Shift character this many pixels right if rendering monospace.
         */
        public final int monoOffset;

        
        private GlyphInfo(int width, int height, int positionX, int positionY, int monoWidth)
        {
            this.positionX = positionX;
            this.positionY = positionY;
            this.pixelWidth = width;
            this.renderWidth = width + horizontalSpacing;
            this.glyphHeight = height;
            int size = RasterFont.this.getWidth();
            
            
            this.uMinNormal = (float) positionX / size;
            this.uMaxNormal = (float) (positionX + width) / size;
            this.vMinNormal = (float) positionY / size;
            this.vMaxNormal = (float) (positionY + height) / size;

            this.uMinMinecraft = 16f * uMinNormal;
            this.uMaxMinecraft = 16f * uMaxNormal;
            this.vMinMinecraft = 16f * vMinNormal;
            this.vMaxMinecraft = 16f * vMaxNormal;

            this.monoOffset = monoWidth > 0 ? (monoWidth - width) / 2 : 0;
        }
        
        /** must be called after texture map creation */
        private void updateInterpolated()
        {
            this.interpolatedMinU = RasterFont.this.getU(this.uMinMinecraft);
            this.interpolatedMaxU = RasterFont.this.getU(this.uMaxMinecraft);
            this.interpolatedMinV = RasterFont.this.getV(this.vMinMinecraft);
            this.interpolatedMaxV = RasterFont.this.getV(this.vMaxMinecraft);
        }
    }
}
