package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class LevelButton extends Button {

    private final String lvl;
    private final float x;
    private final float y;

    public LevelButton(BoxGameReloaded game, String s, float x, float y) {
        super(game);
        this.lvl = s;
        this.x = x;
        this.y = y;
    }

    @Override
    public String text() {
        if (lvl.startsWith("paint_")) {
            return "Paint " + lvl.substring(6);
        }
        return lvl;
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(x, y, -0.01f, x + .2f, y + .2f, 0.025f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        game.joinWorld(lvl);
        game.connection.handleSoon();
        game.gui.closeMainMenu();
        game.pauseLock.unlock();
        return true;
    }
}
