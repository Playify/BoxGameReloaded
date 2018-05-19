package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.util.Finger;

public interface Paintable {
    void draw(int data);

    String name(int data);

    void paint(float x, float y, boolean click, Finger finger);

    boolean canDraw();
}
