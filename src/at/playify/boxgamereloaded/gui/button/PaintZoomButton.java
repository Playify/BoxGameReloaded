package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.paint.PlayPaint;
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
    public BoundingBox3d bound() {
        bound.set(0, 0, 0, 1/7f, 1/7f, 1/7f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        if ((game.vars.debug.console||game.vars.world.startsWith("paint"))
                &&!game.gui.isMainMenuVisible()&&game.painter.draw&&!game.gui.isOptionsVisible()&&!(game.painter.paint() instanceof PlayPaint)) {
            game.painter.quick=false;
            game.painter.zoom^=true;
            game.painter.zoom&=game.connection.pauseCount==0;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void draw(Drawer d) {
        if ((!((game.vars.debug.console||game.painter.draw||game.vars.world.startsWith("paint"))&&game.painter.draw))
                ||game.gui.isMainMenuVisible()||game.gui.isOptionsVisible()||(game.painter.paint() instanceof PlayPaint)) {
            return;
        }
        int color=color();
        final float v=.1f;
        d.cube(0, 0, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 1-v, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 0, 0, v, 1, v, color, false, true, false, true);
        d.cube(1-v, 0, 0, v, 1, v, color, false, true, false, true);
        if (game.painter.zoom) {
            float vv=1/14f;
            float max=1-2*vv;
            d.cube(vv, vv, v/2, max, max, 0, 0xA0FF0000, false, false, false, false);
        }
        d.translate(.5f, .5f, -.2f);
        d.scale(.4f);
        d.translate(0,0,.5f);
        game.d.rotate(45,0,0,1);
        for (int i=0;i<4;i++) {
            float vv=.75f;
            game.d.translate(0,-vv,0);
            game.d.vertex(game.vertex.arrow,0xFF00FF00);
            game.d.translate(0,vv,0);
            game.d.rotate(90,0,0,1);
        }
    }
}
