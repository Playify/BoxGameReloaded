package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.PaintButton;
import at.playify.boxgamereloaded.gui.button.PaintSelectButton;
import at.playify.boxgamereloaded.gui.button.PaintZoomButton;
import at.playify.boxgamereloaded.paint.Paintable;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiDraw extends Gui {
    public boolean quick;
    public float state;
    public boolean zoom;
    private PaintButton paintButton;
    private PaintZoomButton paintZoomButton;

    GuiDraw(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        paintButton=new PaintButton(game);
        paintZoomButton=new PaintZoomButton(game);
        ArrayList<Paintable> p=game.painter.list;
        for (int i=1;i<49;i++) {
            buttons.add(new PaintSelectButton(game, p, i, this));
        }
    }

    @Override
    public boolean click(Finger finger) {
        float x=finger.getX()/game.d.getHeight(), y=1-finger.getY()/game.d.getHeight();
        if (paintButton.bound().contains(x, y)&&paintButton.click(finger)) {
            return true;
        }
        if (paintZoomButton.bound().contains(x, y)&&paintZoomButton.click(finger)) {
            return true;
        }
        return super.click(finger);
    }



    @Override
    public void draw() {
        game.d.pushMatrix();
        BoundingBox3d bound=paintButton.bound();
        game.d.translate(bound.minX, bound.minY, bound.minZ);
        game.d.scale(bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ);
        paintButton.draw(game.d);
        game.d.popMatrix();
        game.d.pushMatrix();
        bound=paintZoomButton.bound();
        game.d.translate(bound.minX, bound.minY, bound.minZ);
        game.d.scale(bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ);
        paintZoomButton.draw(game.d);
        game.d.popMatrix();
        if (state!=0) {
            super.draw();
        }
        if (!game.painter.draw||quick) return;
        String name=game.painter.paint().name(0);
        game.d.drawString(name, game.aspectratio-1/7f-0.01f-game.d.getStringWidth(name)*0.05f, 0.01f, 0.05f);
    }

    @Override
    public boolean tick() {
        if (!game.gui.isOptionsVisible()&&game.gui.drawer.quick&&game.painter.draw) {
            state=Math.min(1f, state+1/4f);
        } else {
            state=Math.max(0, state-1/4f);
        }
        return paintButton.tick()&&(state==0||state==1);
    }
}
