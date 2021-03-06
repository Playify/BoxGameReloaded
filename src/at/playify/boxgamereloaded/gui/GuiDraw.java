package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.paint.PaintButton;
import at.playify.boxgamereloaded.gui.button.paint.PaintSelectButton;
import at.playify.boxgamereloaded.gui.button.paint.PaintZoomButton;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiDraw extends Gui {
    public float state;
    private PaintButton paintButton;
    private PaintZoomButton paintZoomButton;

    GuiDraw(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        paintButton=new PaintButton(game);
        paintZoomButton=new PaintZoomButton(game);
        for (int i=1;i<49;i++) {
            buttons.add(new PaintSelectButton(game, i, this));
        }
    }

    @Override
    public boolean click(Finger finger) {
        if (game.gui.isMainMenuVisible()) return false;
        float x=finger.x/game.d.getHeight(), y=1-finger.y/game.d.getHeight();
        if (paintButton.genBound().contains(x, y)&&paintButton.click(finger)) {
            paintButton.onClick();
            return true;
        }
        if (paintZoomButton.genBound().contains(x, y)&&paintZoomButton.click(finger)) {
            paintZoomButton.onClick();
            return true;
        }
        return super.click(finger);
    }

    @Override
    public void draw() {
        game.d.pushMatrix();
        BoundingBox3d bound=paintButton.genBound();
        game.d.translate(bound.minX, bound.minY, bound.minZ);
        game.d.scale(bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ);
        paintButton.draw(game.d);
        game.d.popMatrix();
        game.d.pushMatrix();
        bound=paintZoomButton.genBound();
        game.d.translate(bound.minX, bound.minY, bound.minZ);
        game.d.scale(bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ);
        paintZoomButton.draw(game.d);
        game.d.popMatrix();
        if (state!=0) {
            super.draw();
        }
        if (!game.painter.draw||game.painter.quick) return;
        String name=game.painter.paint().name(0);
        game.d.drawString(name, game.aspectratio-1/7f-0.01f-game.d.getStringWidth(name)*0.05f, 0.01f, 0.05f, 0x66000000);
    }

    @Override
    public boolean tick() {
        if (!game.gui.isOptionsVisible()&&game.painter.quick&&game.painter.draw) {
            state=Math.min(1f, state+1/4f);
        } else {
            state=Math.max(0, state-1/4f);
        }
        return paintButton.tick()&&(state==0||state==1);
    }
}
