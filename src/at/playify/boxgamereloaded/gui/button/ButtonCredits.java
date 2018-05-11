package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiCredits;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonCredits extends Button {
    public ButtonCredits(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Credits";
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(.25f, .45f, -.05f, game.aspectratio/2-0.025f, .55f, 0);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        game.gui.openMenu(new GuiCredits(game));
        return true;
    }
}
