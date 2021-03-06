package at.playify.boxgamereloaded.gui.button.options;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonCubic extends Button {
    public ButtonCubic(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String genText() {
        return "3 Dimensional";
    }

    @Override
    public BoundingBox3d genBound() {
        buttonBound.set(.3f,.4f,-.05f,game.aspectratio/2-.025f,.5f,0);
        return buttonBound;
    }

    @Override
    public boolean click(Finger finger) {
        game.vars.cubic^=true;
        game.vars.loader.save();
        return true;
    }

    @Override
    public void draw(Drawer d) {
        d.pushMatrix();
        BoundingBox3d bound=genBound();
        d.cube(0, 0, 0, 1, 1, 1, genColor());
        float v=(bound.maxY-bound.minY);
        float v2=(bound.maxX-bound.minX);
        d.scale(1/v2, 1/v,1);
        d.drawStringCenter(genText(), v2/2, v/4, v/2, game.vars.cubic?0xFF00FF00:0xFFFF0000);
        d.popMatrix();
    }
}
