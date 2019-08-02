package grondag.fermion.color;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import grondag.fermion.Fermion;
import grondag.fermion.color.Color.HCLMode;
import grondag.fermion.color.ColorSet.Tone;


public class ColorAtlas
{
    public static final ColorAtlas INSTANCE = new ColorAtlas();
    public static final int COLOR_BASALT = INSTANCE.getColorMap(Hue.COBALT, Chroma.NEUTRAL, Luminance.MEDIUM_DARK).getColor(Tone.BASE);
    public static final int  COLOR_LAVA = INSTANCE.getMostest(Hue.ROSE).getColor(Tone.BASE);
    
    // note: can't be static because must come after Hue static initializaiton
    private final ColorSet[] validColors;
    private final ColorSet[][][] allColors = new ColorSet[Hue.COUNT][Chroma.COUNT][Luminance.COUNT];
    protected ColorAtlas()
    {
        
        ArrayList<ColorSet> colorMaps = new ArrayList<ColorSet>(allColors.length);
        int i=0;
        
        for(int h = 0; h < Hue.COUNT; h++)
        {
            final Hue hue = Hue.VALUES[h];
            
            for(int l = 0; l < Luminance.COUNT; l++)
            {
                final Luminance luminance = Luminance.VALUES[l];
                
                for(int c = 0 ; c < Chroma.COUNT; c++)
                {
                    final Chroma chroma = Chroma.VALUES[c];
                    if(chroma != Chroma.PURE_NETURAL)
                    {
                        Color testColor = Color.fromHCL(hue.hueDegrees(), chroma.value, luminance.value, HCLMode.REDUCE_CHROMA);
                        
                        if(testColor.IS_VISIBLE && testColor.HCL_C > chroma.value - 6)
                        {
                            ColorSet newMap = ColorSet.makeColorMap(hue, chroma, luminance, i++);
                            colorMaps.add(newMap);
                            allColors[hue.ordinal()][chroma.ordinal()][luminance.ordinal()] = newMap;
                        }
                    }
                }
            }
        }
        
        // pure neutrals
        for(int l = 0; l < Luminance.COUNT; l++)
        {
            final Luminance luminance = Luminance.VALUES[l];
            
            Color testColor = Color.fromHCL(Hue.BLUE.hueDegrees(), Chroma.PURE_NETURAL.value, luminance.value, HCLMode.REDUCE_CHROMA);
            
            if(testColor.IS_VISIBLE)
            {
                ColorSet newMap = ColorSet.makeColorMap(Hue.BLUE, Chroma.PURE_NETURAL, luminance, i++);
                colorMaps.add(newMap);

                for(int h = 0; h < Hue.COUNT; h++)
                {
                    allColors[h][Chroma.PURE_NETURAL.ordinal()][l] = newMap;
                }
            }
        }
        
        this.validColors = colorMaps.toArray(new ColorSet[0]);
    }
  
   
    public int getColorMapCount()
    {
        return validColors.length;
    }

    public ColorSet getColorMap(int colorIndex)
    {
        return validColors[Math.max(0, Math.min(validColors.length-1, colorIndex))];
    }
    
    /** may return NULL */
    public ColorSet getColorMap(Hue hue, Chroma chroma, Luminance luminance)
    {
        return allColors[hue.ordinal()][chroma.ordinal()][luminance.ordinal()];
    }

    public ColorSet getMostest(Hue hue)
    {
        ColorSet[][] chromas = allColors[hue.ordinal()];
        for(int i = chromas.length - 1; i >= 0; i--)
        {
            ColorSet[] lums = chromas[i];
            for(int j = lums.length - 1; j >= 0; j--)
            {
                if(lums[j] != null) 
                    return lums[j];
            }
        }
        // safety outlet - should never get here
        assert false :  "Unable to find most intense/brightest color for hue " + hue.toString();
        return validColors[0];
    }

    public static void writeColorAtlas(File folderName)
    {
        File output = new File(folderName, "hard_science_color_atlas.html");
        try
        {
            if(output.exists())
            {
                output.delete();
            }
            output.createNewFile();
    
            FileOutputStream fos = new FileOutputStream(output);
            BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
    
            buffer.write("<head>"); buffer.newLine();
            buffer.write("<style>"); buffer.newLine();
            buffer.write("table {"); buffer.newLine();
            buffer.write("    border-spacing: 1px 1px;"); buffer.newLine();
            buffer.write("}"); buffer.newLine();
            buffer.write("th, td {"); buffer.newLine();
            buffer.write("    height: 23px;"); buffer.newLine();
            buffer.write("    pixelWidth: 130px;"); buffer.newLine();
            buffer.write("    vertical-align: center;");
            buffer.write("}"); buffer.newLine();
            buffer.write("th {"); buffer.newLine();
            buffer.write("    text-align: center;");
            buffer.write("}"); buffer.newLine();
            buffer.write("td {"); buffer.newLine();
            buffer.write("    text-align: right;");
            buffer.write("}"); buffer.newLine();
            buffer.write("</style>"); buffer.newLine();
            buffer.write("</head>"); buffer.newLine();
    
            buffer.write("<table class=\"w3-table-all\">"); buffer.newLine();
            buffer.write("<tr><th>Hue Name</th>");
            buffer.write("<th>RGB</th>");
            buffer.write("<th>H deg</th>");
            buffer.write("</tr>");
            for(Hue h : Hue.values())
            {
                buffer.write("<tr>");
    
                int color = h.hueSample() & 0xFFFFFF;
                buffer.write(String.format("<td style=\"background:#%1$06X\">" + h.localizedName() + "</td>", color));
    
                buffer.write(String.format("<td style=\"background:#%1$06X\">" + Integer.toHexString(color) + "</td>", color));
    
                buffer.write(String.format("<td style=\"background:#%1$06X\">" + Math.round(h.hueDegrees()) + "</td>", color));
                buffer.write("</tr>");
            }
            buffer.write("</table>");
            buffer.write("<h1>&nbsp;</h1>");
            buffer.close();
            fos.close();
    
        }
        catch (IOException e)
        {
            Fermion.LOG.warn("Unable to output color atlas due to file error:" + e.getMessage());
        }
    }
}
