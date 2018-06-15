package at.playify.boxgamereloaded.gui.button.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiDraw;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.paint.Paintable;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class PaintSelectButton extends Button {
    private final int index;
    private final GuiDraw gui;

    public PaintSelectButton(BoxGameReloaded game, int index, GuiDraw gui) {
        super(game);
        this.index=--index;
        this.gui=gui;
    }

    @Override
    public String genText() {
        return "Draw:"+index;
    }

    @Override
    public BoundingBox3d genBound() {
        int a=7;
        int index=this.index+1;
        float x=1/7f+gui.state*((index%a+1)/(float) a-1/7f);
        float y=gui.state*((index/a+1)/(float) a-1/7f);
        buttonBound.set(game.aspectratio-x, y, -0.01f, game.aspectratio-x+1f/a, y+1f/a, 1f/a-0.01f);
        return buttonBound;
    }

    @Override
    public boolean click(Finger finger) {
        if (gui.state!=1) return false;
        Paintable paint=game.painter.get(index);
        if (paint==null) return false;
        game.painter.paint(paint);
        game.painter.quick=false;
        game.painter.zoom=false;
        return true;
    }

    @Override
    public void draw(Drawer d) {
        if (gui.state==0) return;
        Paintable paint=game.painter.get(index);
        if (paint==null) return;
        int color=genColor();
        final float v=.1f;
        d.cube(0, 0, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 1-v, 0, 1, v, v, color, true, false, true, false);
        d.cube(0, 0, 0, v, 1, v, color, false, true, false, true);
        d.cube(1-v, 0, 0, v, 1, v, color, false, true, false, true);
        d.translate(.5f, .5f, -.2f);
        d.scale(.4f);
        d.translate(-.5f, -.5f, 0);
        boolean back=game.d.back();
        game.d.back(true);
        paint.draw(0);
        game.d.back(back);
    }
}
