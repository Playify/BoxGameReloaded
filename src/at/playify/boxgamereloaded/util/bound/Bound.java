package at.playify.boxgamereloaded.util.bound;

import at.playify.boxgamereloaded.level.Level;

public interface Bound {
    boolean collide(RectBound b);
    boolean collide(PointBound b);
    boolean collide(CircleBound b);

    void move(float x, float y);
    void size(RectBound b);
    boolean inLevel(Level level);
    float cx();
    float cy();
}
