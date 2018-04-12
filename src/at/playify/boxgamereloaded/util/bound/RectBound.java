package at.playify.boxgamereloaded.util.bound;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.util.Utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "unused"})
//Rechteckige KollisionsBox
public class RectBound implements Serializable, Cloneable , Bound<RectBound>{
    private static final DecimalFormat dmf = new DecimalFormat("00.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final DecimalFormat dm = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final float FACTOR=100;
    int x;
    int y;
    int w;
    int h;

    public RectBound(float x, float y, float w, float h) {
        this.x(x);
        this.y(y);
        this.w(w);
        this.h(h);
    }

    public RectBound() {
        x(y(w(h(Float.NaN))));
    }

    public static RectBound parse(String simple) {
        String[] s = simple.split(" ");
        return new RectBound(Utils.parseFloat(s[0]), Utils.parseFloat(s[1]), Utils.parseFloat(s[2]), Utils.parseFloat(s[3]));
    }

    public RectBound set(float x, float y) {
        this.x(x);
        this.y(y);
        return this;
    }

    public RectBound multi(float add) {
        x(x()-add);
        y(y()-add);
        w(w()+add * 2);
        h(h()+add * 2);
        return this;
    }

    public boolean collide(RectBound b) {
        return x()< b.xw()&& xw()>b.x()&& y()< b.yh()&& yh()>b.y();
    }

    @Override
    public boolean collide(PointBound b) {
        return x()< b.x && x()+w()> b.x && y()< b.y && h()+y()> b.y;
    }

    @Override
    public boolean collide(CircleBound b) {
        return b.collide(this);
    }


    public RectBound copy() {
        return new RectBound(x(), y(), w(), h());
    }

    public RectBound move(float x, float y) {
        this.x(this.x()+x);
        this.y(this.y()+y);
        return this;
    }

    @Override
    public void size(RectBound b) {
        b.set(this);
    }

    public String toString() {
        return "Bound(" +x()+ "," +y()+ "," +w()+ "," +h()+ ")";
    }

    public boolean inLevel(Level level) {
        return x()>= 0 && y()>= 0 &&
                x()+w()<= level.sizeX &&
                y()+h()<= level.sizeY;
    }

    public boolean contains(float x, float y) {
        return this.x()< x && this.y()< y &&
                this.x()+w()> x && this.y()+h()> y;
    }

    public RectBound set(int x, int y, int w, int h) {
        this.x(x);
        this.y(y);
        this.w(w);
        this.h(h);
        return this;
    }

    public float cx() {
        return (x+w/2)/FACTOR;
    }

    public float cy() {
        return (y+h/2)/FACTOR;
    }

    public RectBound set(float x, float y, float w, float h) {
        this.x(x);
        this.y(y);
        this.w(w);
        this.h(h);
        return this;
    }

    public RectBound set(RectBound b) {
        x(b.x());
        y(b.y());
        w(b.w());
        h(b.h());
        return this;
    }


    public void setNaN(float x, float y, float w, float h) {
        if (!Float.isNaN(x)) this.x(x);
        if (!Float.isNaN(y)) this.y(y);
        if (!Float.isNaN(w)) this.w(w);
        if (!Float.isNaN(h)) this.h(h);
    }

    public String toSimpleString(String delim, boolean fixed) {
        if (fixed)
            return dmf.format(x()) + delim + dmf.format(y()) + delim + dmf.format(w()) + delim + dmf.format(h());
        else
            return dm.format(x()) + delim + dm.format(y()) + delim + dm.format(w()) + delim + dm.format(h());
    }

    public RectBound setCenter(float x, float y) {
        this.x(x - w()/ 2);
        this.y(y - h()/ 2);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RectBound)) return false;
        RectBound b = (RectBound) obj;
        return x()==b.x()&&
                y()==b.y()&&
                w()==b.w()&&
                h()==b.h();
    }

    public RectBound round() {
        x(Utils.round2(x()));
        y(Utils.round2(y()));
        return this;
    }

    private void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.writeFloat(x());
        stream.writeFloat(y());
        stream.writeFloat(w());
        stream.writeFloat(h());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        x(stream.readFloat());
        y(stream.readFloat());
        w(stream.readFloat());
        h(stream.readFloat());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new float[]{x(), y(), w(), h()});
    }

    public PointBound center(PointBound b) {
        return b.set(cx(), cy());
    }

    public RectBound sizeOf(Bound b) {
        b.size(this);
        return this;
    }

    public float x() {
        return x/FACTOR;
    }

    public float x(float x) {
        this.x=Math.round(x*FACTOR);
        return x;
    }

    public float y() {
        return y/FACTOR;
    }

    public float y(float y) {
        this.y=Math.round(y*FACTOR);
        return y;
    }

    public float w() {
        return w/FACTOR;
    }

    public float w(float w) {
        this.w=Math.round(w*FACTOR);
        return w;
    }

    public float h() {
        return h/FACTOR;
    }

    public float h(float h) {
        this.h=Math.round(h*FACTOR);
        return h;
    }

    public float xw() {
        return (x+w)/FACTOR;
    }

    public float yh() {
        return (y+h)/FACTOR;
    }

    public RectBound addCoord(float x, float y) {
        if(x>0){
            this.x+=x*FACTOR;
        }
        this.w+=Math.abs(x*FACTOR);
        if(y>0){
            this.y+=y*FACTOR;
        }
        this.h+=Math.abs(y*FACTOR);
        return this;
    }

    public float calculateXOffset(RectBound bound, float offsetX) {
        if (bound.y+bound.h > this.y && bound.y < this.y+this.h)
        {
            offsetX*=FACTOR;
            if (offsetX > 0.0f && bound.x+bound.w <= this.x)
            {
                float d1 = this.x - bound.x+bound.w;

                if (d1 < offsetX)
                {
                    offsetX = d1;
                }
            }
            else if (offsetX < 0.0f && bound.x >= this.x+this.w)
            {
                float d0 = this.x+this.w - bound.x;

                if (d0 > offsetX)
                {
                    offsetX = d0;
                }
            }

            return offsetX;
        }
        else
        {
            return offsetX;
        }
    }

    public float calculateYOffset(RectBound bound, float offsetY) {
        if (bound.x+bound.w > this.x && bound.x < this.x+this.w)
        {
            if (offsetY > 0.0f && bound.y+bound.h <= this.y)
            {
                float d1 = this.y - bound.y+bound.h;

                if (d1 < offsetY)
                {
                    offsetY = d1;
                }
            }
            else if (offsetY < 0.0f && bound.y >= this.y+this.h)
            {
                float d0 = this.y+this.h - bound.y;

                if (d0 > offsetY)
                {
                    offsetY = d0;
                }
            }

            return offsetY;
        }
        else
        {
            return offsetY;
        }
    }
}

