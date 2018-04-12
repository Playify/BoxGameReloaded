package at.playify.boxgamereloaded.util.bound;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.util.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

//Rechteckige KollisionsBox mit int (frühere Implementierung, durch verbesserte Kollision überflüssig)
public class IntBound {
    private static final DecimalFormat dmf = new DecimalFormat("00.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final DecimalFormat dm = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final int FACTOR=100;
    public int x;
    public int y;
    public int w;
    public int h;

    public IntBound(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public IntBound() {
        x = y = w = h = 0;
    }

    public static RectBound parse(String simple) {
        String[] s = simple.split(" ");
        return new RectBound(Utils.parseFloat(s[0]), Utils.parseFloat(s[1]), Utils.parseFloat(s[2]), Utils.parseFloat(s[3]));
    }

    public IntBound set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public boolean collide(IntBound b) {
        return x < b.x + b.w && x + w > b.x && y < b.y + b.h && h + y > b.y;
    }

    public IntBound move(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public void size(RectBound b) {
        b.set(x/(float)FACTOR,y/(float)FACTOR,w/(float)FACTOR,h/(float)FACTOR);
    }

    public String toString() {
        return "Bound(" + x + "," + y + "," + w + "," + h + ")";
    }

    public boolean inLevel(Level level) {
        return x >= 0 && y >= 0 &&
                x + w <= level.sizeX*FACTOR &&
                y + h <= level.sizeY*FACTOR;
    }

    public boolean contains(float x, float y) {
        return this.x < x && this.y < y &&
                this.x + w > x && this.y + h > y;
    }
    public boolean contains(int x, int y) {
        return this.x < x && this.y < y &&
                this.x + w > x && this.y + h > y;
    }

    public int cx() {
        return x + w / 2;
    }

    public int cy() {
        return y + h / 2;
    }

    public IntBound set(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }

    public IntBound set(RectBound b) {
        x = (int)(b.x()*FACTOR);
        y = (int)(b.y()*FACTOR);
        w = (int)(b.w()*FACTOR);
        h = (int)(b.h()*FACTOR);
        return this;
    }

    public IntBound setCenter(int x, int y) {
        this.x = x - w / 2;
        this.y = y - h / 2;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntBound)) return false;
        IntBound b = (IntBound) obj;
        return x == b.x &&
                y == b.y &&
                w == b.w &&
                h == b.h;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new float[]{x, y, w, h});
    }

    public PointBound center(PointBound b) {
        return b.set(cx(), cy());
    }

}

