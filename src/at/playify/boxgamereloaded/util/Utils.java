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
}
