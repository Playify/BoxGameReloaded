package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public abstract class Button implements Comparable<Button> {
    protected final BoxGameReloaded game;
    protected BoundingBox3d bound = new BoundingBox3d(0, 0, 0, 0, 0, 0);

    public Button(BoxGameReloaded game) {
        this.game = game;
    }

    public abstract String text();

    public int color() {
        return 0xFF005C7A;
    }

    public abstract BoundingBox3d bound();

    public void draw(Drawer d) {
        BoundingBox3d bound = bound();
        d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX - bound.minX, bound.maxY - bound.minY, bound.maxZ - bound.minZ, color());
        d.drawStringCenter(text(), (bound.minX + bound.maxX) / 2, (bound.minY + bound.maxY) / 2, .05f);
    }

    @Override
    public int compareTo(Button o) {
        return Float.compare(bound().maxZ, o.bound().maxZ);
    }

    public abstract boolean click(Finger finger);
}
