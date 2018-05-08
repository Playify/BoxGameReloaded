package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class PaintButton extends Button {
    public PaintButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Draw";
    }

    @Override
    public BoundingBox3d bound() {
        float z=1/7f;
        bound.set(game.aspectratio-z, 0, 0, game.aspectratio, z, z);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        if ((!((game.vars.debug.console||game.painter.draw||game.vars.world.startsWith("paint"))&&(game.gui.isOptionsVisible()||game.painter.draw)))||game.gui.isMainMenuVisible()) {
            game.painter.quick=false;
            return;
        }
        int color=color();
        final float v=.1f;
        d.cube(0, 0, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 1-v, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 0, 0, v, 1, v, color, false, true, false, true);
        d.cube(1-v, 0, 0, v, 1, v, color, false, true, false, true);
        if (game.painter.draw) {
            d.translate(.5f, .5f, -.2f);
            d.scale(.4f);
            d.translate(-.5f, -.5f, 0);
            game.d.back(true);
            game.painter.paint().draw(0);
            game.d.back(false);
        }
    }

    @Override
    public boolean click(Finger finger) {
        if ((game.vars.debug.console||game.vars.world.startsWith("paint"))&&!game.gui.isMainMenuVisible()) {
            if (game.gui.isOptionsVisible()) {
                if (game.painter.draw) {
                    game.painter.draw=false;
                    game.painter.quick=false;
                    game.painter.zoom=false;
                } else {
                    game.gui.closeOptions();
                    game.painter.draw=true;
                    game.painter.quick=true;
                }
            } else if (game.painter.draw) {
                game.painter.quick^=true;
            }
            game.painter.quick&=game.connection.pauseCount==0;
            game.painter.zoom&=game.connection.pauseCount==0;
            return true;
        } else {
            game.painter.draw=false;
            game.painter.quick=false;
            game.painter.zoom=false;
            game.gui.drawer.state=0;
            return false;
        }
    }
}