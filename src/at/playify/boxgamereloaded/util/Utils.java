package at.playify.boxgamereloaded.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

//Hilfsmethoden
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "unused"})
public class Utils {
    private static final DecimalFormat dm = new DecimalFormat("#.##");

    static {
        dm.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    public static float parseFloat(String s) {
        return parseFloat(s, Float.NaN);
    }

    public static float parseFloat(String s, float def) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static int parseHex(String s, int def) {
        try {
            return (int) Long.parseLong(s, 16);
        } catch (Exception e) {
            return def;
        }
    }

    public static long parseLong(String s) {
        return parseLong(s, 0);
    }

    public static long parseLong(String s, long def) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static float round2(float f) {
        return Math.round(f*100f)/100f;
    }


    public static float clamp(float val, float min, float max) {
        return val < min ? min : val > max ? max : val;
    }

    public static int clamp(int val, int min, int max) {
        return val < min ? min : val > max ? max : val;
    }

    public static int hsvToRgb(float hue) {
        float saturation=1,value=1;
        hue%=1;
        int h = (int)(hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

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

    public static int rgbFloatToInt(float r, float g, float b) {
        int rr= (int) (255*r),gg= (int) (255*g),bb= (int) (255*b);
        return 0xFF000000|(rr<<16)|(gg<<8)|(bb);
    }

    public static int round(float v) {
        return Math.round(v);
    }

    public static int color(int meta, int states) {
        if (states==6) {
            return hsvToRgb(meta/(float)states);
        }else if (states==8){
            meta%=8;
            if(meta<0)meta+=8;
            switch (meta){
                case 0:return 0xFFFF0000;
                case 1:return 0xFFFF8000;
                case 2:return 0xFFFFFF00;
                case 3:return 0xFF00FF00;
                case 4:return 0xFF00FFFF;
                case 5:return 0xFF0000FF;
                case 6:return 0xFFAA00FF;
                case 7:return 0xFFFF00FF;
                default:return 0xFFFF0000;
            }
        }else{
            return hsvToRgb(meta/(float)states);
        }
    }

    public static String inputStreamToString(InputStream inputStream){
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
