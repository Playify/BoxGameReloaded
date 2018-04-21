package at.playify.boxgamereloaded.util.bound;


import at.playify.boxgamereloaded.level.Level;

//Punktförmige KollisionsBox
public class PointBound implements Bound<PointBound>{
    public float x;
    public float y;

    public PointBound(float x, float y) {
        this.x=x;
        this.y=y;
    }

    public PointBound() {}

    @Override
    public boolean collide(RectBound b) {
        return b.contains(x,y);
    }

    public boolean collide(PointBound b) {
        return b.x == x && b.y == y;
    }

    @Override
    public boolean collide(CircleBound b) {
        return b.distanceTo(x,y)<=b.r;
    }


    public PointBound set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public PointBound set(PointBound p) {
        x = p.x;
        y = p.y;
        return this;
    }

    public PointBound move(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public void size(RectBound b) {
        b.set(x,y,0,0);
    }

    public boolean inLevel(Level level) {
        return x >= 0 && y >= 0 &&
                x <= level.sizeX &&
                y <= level.sizeY;
    }

    @Override
    public float cx() {
        return x;
    }

    @Override
    public float cy() {
        return y;
    }


    public float distanceTo(float x, float y) {
        x -= this.x;
        y -= this.y;
        return (float) Math.sqrt(x * x + y * y);
    }

}
