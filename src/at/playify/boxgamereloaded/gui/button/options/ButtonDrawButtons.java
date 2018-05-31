package at.playify.boxgamereloaded.gui.button.options;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonDrawButtons extends Button {
    public ButtonDrawButtons(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Steuerung zeigen";
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(.3f,.25f,-.05f,game.aspectratio/2-.025f,.35f,0);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        game.vars.buttons^=true;
        game.vars.loader.save();
        return true;
    }

    @Override
    public void draw(Drawer d) {
        d.pushMatrix();
        BoundingBox3d bound=bound();
        d.cube(0, 0, 0, 1, 1, 1, color());
        float v=(bound.maxY-bound.minY);
        float v2=(bound.maxX-bound.minX);
        d.scale(1/v2, 1/v,1);
        d.drawStringCenter(text(), v2/2, v/4, v/2, game.vars.buttons?0xFF00FF00:0xFFFF0000);
        d.popMatrix();
    }
}
