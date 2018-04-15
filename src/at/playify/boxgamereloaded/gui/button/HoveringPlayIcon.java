package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class HoveringPlayIcon extends Button {
    private int backgroundState;

    public HoveringPlayIcon(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "PlayIcon";
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(-1, -1, -1, 2, 1 + game.aspectratio, -1);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        if (backgroundState != 0) {
            game.d.depth(false);
            d.rect(0, 0, 1, 1, color());
            game.d.depth(true);
        }
    }

    @Override
    public int color() {
        return ((int) (0.3 * backgroundState) << 24);
    }

    @Override
    public boolean click(Finger finger) {
        return false;
    }

    @Override
    public boolean tick() {
        if (game.paused || game.options || (game.connection == null || game.connection.isPaused(true))) {
            backgroundState = Math.min(255, backgroundState + 32);
        } else {
            backgroundState = Math.max(0, backgroundState - 32);
        }
        return backgroundState == 0 || backgroundState == 255;
    }
}
