package at.playify.boxgamereloaded.util.bound;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.util.Utils;

//Kreisförmige KollisionsBox
@SuppressWarnings("WeakerAccess")
public class CircleBound implements Bound<CircleBound>{
    public float x;
    public float y;
    public float r;

    public CircleBound set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }


    public boolean collide(RectBound b) {
        return distanceTo(Utils.clamp(x, b.x(), b.x()+b.w()),Utils.clamp(y, b.y(), b.y()+b.h())) < r;
    }

    public boolean collide(CircleBound b) {
        return distanceTo(b.x, b.y) - b.r - r < 0;
    }

    public boolean collide(PointBound b) {
        return distanceTo(b.x, b.y) - r < 0;
    }


    public float distanceTo(float x, float y) {
        x -= this.x;
        y -= this.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    public CircleBound set(CircleBound b) {
        x = b.x;
        y = b.y;
        r = b.r;
        return this;
    }

    public CircleBound move(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public void size(RectBound b) {
        b.set(x*-r,y-r,r*2,r*2);
    }

    public boolean inLevel(Level level) {
        return x - r >= 0 && y - r >= 0 &&
                x + r <= level.sizeX &&
                y + r <= level.sizeY;
    }

    @Override
    public float cx() {
        return x;
    }

    @Override
    public float cy() {
        return y;
    }

    public CircleBound set(float x, float y, float r) {
        this.x=x;
        this.y=y;
        this.r=r;
        return this;
    }
}
