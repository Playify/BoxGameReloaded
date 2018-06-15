package at.playify.boxgamereloaded.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

//Hilfsmethoden
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class Utils {
    private static final DecimalFormat dm=new DecimalFormat("#.##");

    static{
        dm.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    private static Random random=new Random();

    public static float parseFloat(String s){
        return parseFloat(s, Float.NaN);
    }

    public static float parseFloat(String s, float def){
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseInt(String s, int def){
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseHex(String s, int def){
        try {
            return (int) Long.parseLong(s, 16);
        } catch (Exception e) {
            return def;
        }
    }

    public static long parseLong(String s){
        return parseLong(s, 0);
    }

    public static long parseLong(String s, long def){
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static float round2(float f){
        return Math.round(f*100f)/100f;
    }


    public static float clamp(float val, float min, float max){
        return val<min ? min : val>max ? max : val;
    }

    public static int clamp(int val, int min, int max){
        return val<min ? min : val>max ? max : val;
    }

    public static int hsvToRgb(float hue){
        final float saturation=1, value=1;
        hue%=1;
        int h=(int) (hue*6);
        float f=hue*6-h;
        float p=value*(1-saturation);
        float q=value*(1-f*saturation);
        float t=value*(1-(1-f)*saturation);

        switch (h) {
            case 0: return rgbFloatToInt(value, t, p);
            case 1: return rgbFloatToInt(q, value, p);
            case 2: return rgbFloatToInt(p, value, t);
            case 3: return rgbFloatToInt(p, q, value);
            case 4: return rgbFloatToInt(t, p, value);
            case 5: return rgbFloatToInt(value, p, q);
        }
        return 0xFF000000;
    }

    public static int blendColors(float progress, int... colors){
        if (colors.length==0) {
            throw new RuntimeException("Error blending Color: Empty Color Array");
        }if (colors.length==1) {
            return colors[0];
        }
        float[] fractions=new float[colors.length];
        for (int i=0;i<fractions.length;i++) {
            fractions[i]=i/(colors.length-1f);
        }

        int startPoint=0;
        while (startPoint<fractions.length&&fractions[startPoint]<=progress) startPoint++;
        startPoint=Math.min(startPoint, fractions.length-1);

        float rgb1[]=toFloatArray(colors[startPoint-1]);
        float rgb2[]=toFloatArray(colors[startPoint]);

        float weight=(progress-fractions[startPoint-1])/(fractions[startPoint]-fractions[startPoint-1]);

        float red=clamp(rgb1[0]*(1-weight)+rgb2[0]*weight, 0, 1);
        float green=clamp(rgb1[1]*(1-weight)+rgb2[1]*weight, 0, 1);
        float blue=clamp(rgb1[2]*(1-weight)+rgb2[2]*weight, 0, 1);
        float alpha=clamp(rgb1[3]*(1-weight)+rgb2[3]*weight, 0, 1);
        return rgbFloatToInt(red, green, blue, alpha);
    }

    private static float[] toFloatArray(int color){
        float a=((color >> 24)&255)/255f, r=((color >> 16)&255)/255f, g=((color >> 8)&255)/255f, b=((color)&255)/255f;
        return new float[]{r, g, b, a};
    }

    public static int rgbFloatToInt(float r, float g, float b){
        int rr=(int) (255*r), gg=(int) (255*g), bb=(int) (255*b);
        return 0xFF000000|(rr<<16)|(gg<<8)|(bb);
    }

    public static int rgbFloatToInt(float r, float g, float b, float a){
        int rr=(int) (255*r), gg=(int) (255*g), bb=(int) (255*b), aa=(int) (255*a);
        return (aa<<24)|(rr<<16)|(gg<<8)|(bb);
    }

    public static int round(float v){
        return Math.round(v);
    }

    public static int color(int meta, int states){
        if (states==6) {
            return hsvToRgb(meta/(float) states);
        } else if (states==8) {
            meta%=8;
            if (meta<0) meta+=8;
            switch (meta) {
                case 0: return 0xFFFF0000;
                case 1: return 0xFFFF8000;
                case 2: return 0xFFFFFF00;
                case 3: return 0xFF00FF00;
                case 4: return 0xFF00FFFF;
                case 5: return 0xFF0000FF;
                case 6: return 0xFFAA00FF;
                case 7: return 0xFFFF00FF;
                default: return 0xFFFF0000;
            }
        } else {
            return hsvToRgb(meta/(float) states);
        }
    }

    public static String inputStreamToString(InputStream inputStream){
        try (ByteArrayOutputStream result=new ByteArrayOutputStream()) {
            byte[] buffer=new byte[1024];
            int length;
            while ((length=inputStream.read(buffer))!=-1) {
                result.write(buffer, 0, length);
            }

            return result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float randomFloat(){
        return random.nextFloat();
    }
    public static int randomInt(){
        return random.nextInt();
    }
}
