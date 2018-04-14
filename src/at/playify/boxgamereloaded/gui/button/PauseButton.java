package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class PauseButton extends Button {

    public PauseButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Pause";
    }

    @Override
    public BoundingBox3d bound() {
        float s = 1 / 6f;
        float aspect = game.d.getWidth() / game.d.getHeight();
        bound.set(aspect - s, 1 - s, 0, aspect, 1, .025f);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        int color = color();
        d.cube(0, 0, 0, 1, 1 / 7f, 1, color, true, true, true, true);
        d.cube(0, 6 / 7f, 0, 1, 1 / 7f, 1, color, true, true, game.pauseState == 0, true);
        d.cube(0, 1 / 7f, 0, 1 / 7f, 5 / 7f, 1, color, game.pauseState == 0, true, false, true);
        d.cube(6 / 7f, 1 / 7f, 0, 1 / 7f, 5 / 7f, 1, color, false, true, false, true);
        if (game.pauseState == 0) {
            d.cube(4 / 10f, 1 / 5f - 1 / 7f, 0, 1 / 5f, 3 / 5f + 2 / 7f, 1, color, false, true, false, true);
        } else {
            d.translate(1 / 2f, 1 / 2f);
            float v = 1 - .5f * game.pauseState;
            for (int i = 0; i < 2; i++) {
                d.pushMatrix();
                d.cube(-1 / 10f, game.pauseState / 4f, 0, 1 / 5f, 3 / 7f * v, 1, color, true, true, true, true);
                d.translate(1 / 7f - 1 / 2f, 1 / 7f - 1 / 2f);
                d.rotate(26.565f * game.pauseState, 0, 0, 1);
                d.cube(0, -1 / 7f, 0, 6 / 7f, 1 / 7f, 1, color, true, false, false, false);
                d.cube(2.9f / 7f, -2.2f / 7f * game.pauseState, 0, 2.2f / 7f, 1.2f / 7f * game.pauseState, 1, color, false, false, false, false);
                d.popMatrix();
                d.scale(1, -1, 1);
            }
        }
    }

    @Override
    public boolean click(Finger finger) {
        game.paused ^= true;
        return true;
    }
}

