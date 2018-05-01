package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ScrollList extends Button {
    public ScrollList(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "List";
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(game.aspectratio/2, -.1f, 0, game.aspectratio, 1.1f, .1f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        return false;
    }
}
