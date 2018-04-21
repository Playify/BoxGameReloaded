package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ConsoleButton extends Button {
    public ConsoleButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Console";
    }

    @Override
    public BoundingBox3d bound() {
        float z=.1f;
        bound.set(0, 0, 0, z, z, z);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        if (!game.vars.debug.console) {
            return;
        }
        int color=color();
        final float v=.1f;
        d.pushMatrix();
        d.cube(0, 0, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 1-v, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 0, 0, v, 1, v, color, false, true, false, true);
        d.cube(1-v, 0, 0, v, 1, v, color, false, true, false, true);
        d.translate(.5f, .5f);
        d.rotate(-45, 0, 0, 1);
        d.cube(-v/2, -v/2-.3f, 0, v, .3f+v, v, color, false, true, true, true);
        d.cube(-v/2-.3f, -v/2, 0, .3f+v, v, v, color, true, false, true, true);
        d.popMatrix();
        if (((System.currentTimeMillis()/400)&1)==0) {
            d.cube(.5f, .2f, 0, .3f, v, v, color);
        }
    }

    @Override
    public boolean click(Finger finger) {
        if (game.vars.debug.console) {
            game.handler.setKeyboardVisible(true);
            return true;
        } else {
            return false;
        }
    }
}
