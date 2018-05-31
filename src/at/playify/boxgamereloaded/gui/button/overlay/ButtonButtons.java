package at.playify.boxgamereloaded.gui.button.overlay;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonButtons extends Button {
    public ButtonButtons(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Buttons";
    }

    @Override
    public BoundingBox3d bound() {
        if (game.gui==null||game.gui.isMainMenuVisible()||!game.vars.buttons) {
            bound.set(0, 0, 0, 0, 0, 0);
        } else {
            bound.set(0, 0, 0, game.aspectratio, 1, 0);
        }
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        return false;
    }

    @Override
    public void draw(Drawer d) {
        if (bound.isEmpty()) return;
        game.d.pushMatrix();
        game.d.scale(1/4f);
        game.d.drawButtons();
        game.d.scale(1f/game.aspectratio, 1, 1);
        float x=game.aspectratio/2;
        game.d.drawStringCenter("LEFT",x,.1f,.2f,0x4B000000);
        game.d.drawStringCenter("RIGHT",3*x,.1f,.2f,0x4B000000);
        game.d.drawStringCenter("JUMP",6*x,.1f,.2f,0x4B000000);
        game.d.popMatrix();
    }
}
