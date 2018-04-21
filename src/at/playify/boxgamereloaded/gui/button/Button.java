package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public abstract class Button implements Comparable<Button> {
    protected final BoxGameReloaded game;
    protected final BoundingBox3d bound=new BoundingBox3d(0, 0, 0, 0, 0, 0);

    public Button(BoxGameReloaded game) {
        this.game = game;
    }

    public abstract String text();

    public int color() {
        return 0xFF005C7A;
    }

    public abstract BoundingBox3d bound();

    public void draw(Drawer d) {
        d.cube(0, 0, 0, 1, 1, 1, color());
        d.drawStringCenter(text(), .5f, .4f, .1f);
    }

    @Override
    public int compareTo(Button o) {
        return Float.compare(bound().maxZ, o.bound().maxZ);
    }

    public abstract boolean click(Finger finger);

    //Rückgabewert bedeutet: Kann pausiert werden
    //d.h. während animation false zurückgeben
    public boolean tick() {
        return true;
    }
}
