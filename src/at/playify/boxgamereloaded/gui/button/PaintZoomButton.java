package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class PaintZoomButton extends Button {

    public PaintZoomButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Zoom";
    }

    @Override
    public BoundingBox3d bound() {//max-v*(min-max)
        bound.set(0, 0, 0, 1/7f, 1/7f, 1/7f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        if ((game.vars.debug.console||game.vars.world.startsWith("paint_"))&&!game.gui.isMainMenuVisible()&&game.painter.draw&&!game.gui.isOptionsVisible()) {
            game.gui.drawer.quick=false;
            game.gui.drawer.zoom^=true;
            game.gui.drawer.zoom&=game.connection.pauseCount==0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void draw(Drawer d) {
        if ((!((game.vars.debug.console||game.painter.draw||game.vars.world.startsWith("paint_"))&&(game.gui.isOptionsVisible()||game.painter.draw)))||game.gui.isMainMenuVisible()) {
            return;
        }
        int color=color();
        final float v=.1f;
        //d.rotate(100,0,1,0);
        d.cube(0, 0, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 1-v, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 0, 0, v, 1, v, color, false, true, false, true);
        d.cube(1-v, 0, 0, v, 1, v, color, false, true, false, true);
        if (game.gui.drawer.zoom) {
            float vv=1/14f;
            float max=1-2*vv;
            d.cube(vv, vv, v/2, max, max, 0, 0xA0FF0000, false, false, false, false);
        }
    }
}
