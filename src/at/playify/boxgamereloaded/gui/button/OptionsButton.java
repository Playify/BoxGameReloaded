package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class OptionsButton extends Button {
    public OptionsButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Options";
    }

    @Override
    public BoundingBox3d bound() {
        float s = 1 / 6f;
        bound.set(0, 1 - s, 0, s, 1, .025f);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        int color = color();
        d.cube(0, 0, 0, 1, 1 / 7f, 1, color, true, false, true, false);
        d.cube(0, 6 / 7f, 0, 1, 1 / 7f, 1, color, true, false, true, false);
        d.cube(0, 0, 0, 1 / 7f, 1, 1, color, false, true, false, true);
        d.cube(6 / 7f, 0, 0, 1 / 7f, 1, 1, color, false, true, false, true);
        d.translate(1 / 2f, 1 / 2f);
        d.rotate(-game.settingsRotate, 0, 0, 1);
        for (int i = 0; i < 4; i++) {
            d.cube(-3 / 14f, -3 / 14f, 0, 1 / 7f, 3 / 7f, 1, color, false, true, false, true);
            d.rotate(90, 0, 0, 1);
        }
    }

    @Override
    public boolean click(Finger finger) {
        game.options ^= true;
        return true;
    }
}
