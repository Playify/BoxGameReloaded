package at.playify.boxgamereloaded.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

//Hilfsmethoden
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

    public static int hsvToRgb(float hue) {
        float saturation=1,value=1;
        hue%=1;
        int h = (int)(hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: return rgbToString(value, t, p);
            case 1: return rgbToString(q, value, p);
            case 2: return rgbToString(p, value, t);
            case 3: return rgbToString(p, q, value);
            case 4: return rgbToString(t, p, value);
            case 5: return rgbToString(value, p, q);
        }
        return 0xFF000000;
    }

    public static int rgbToString(float r, float g, float b) {
        int rr= (int) (255*r),gg= (int) (255*g),bb= (int) (255*b);
        return 0xFF000000|(rr<<16)|(gg<<8)|(bb<<0);
    }
}
