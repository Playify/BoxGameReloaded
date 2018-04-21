package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiDraw;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.paint.Paintable;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class PaintSelectButton extends Button {
    private final int index;
    private final GuiDraw gui;
    public Paintable paint;

    public PaintSelectButton(BoxGameReloaded game, int index, GuiDraw gui) {
        super(game);
        this.index=--index;
        this.gui=gui;
        if (index<game.painter.list.size()) {
            paint=game.painter.list.get(index);
        }
    }

    @Override
    public String text() {
        return "Draw:"+index;
    }

    @Override
    public BoundingBox3d bound() {//max-v*(min-max)
        int a=7;
        int index=this.index+1;
        float x=1/7f+gui.state*((index%a+1)/(float) a-1/7f);
        float y=gui.state*((index/a+1)/(float) a-1/7f);
        bound.set(game.aspectratio-x, y, -0.01f, game.aspectratio-x+1f/a, y+1f/a, 1f/a-0.01f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        if (gui.state!=1) return false;
        game.painter.paint(paint);
        game.gui.drawer.quick=false;
        return true;
    }

    @Override
    public void draw(Drawer d) {
        if (gui.state==0) {
            return;
        }
        int color=color();
        final float v=.1f;
        //d.rotate(100,0,1,0);
        d.cube(0, 0, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 1-v, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 0, 0, v, 1, v, color, false, true, false, true);
        d.cube(1-v, 0, 0, v, 1, v, color, false, true, false, true);
        d.translate(.5f, .5f, -.2f);
        d.scale(.4f);
        d.translate(-.5f, -.5f, 0);
        if (paint!=null) {
            game.d.back(true);
            paint.draw(0);
            game.d.back(false);
        }
    }
}
