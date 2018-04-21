package at.playify.boxgamereloaded.util.bound;

import at.playify.boxgamereloaded.level.Level;

public interface Bound<T> {
    boolean collide(RectBound b);
    boolean collide(PointBound b);
    boolean collide(CircleBound b);

    T move(float x, float y);
    void size(RectBound b);
    boolean inLevel(Level level);
    float cx();
    float cy();
}
