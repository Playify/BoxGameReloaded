package at.playify.boxgamereloaded.gui.button.options;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiChangelog;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonChangelog extends Button {
    public ButtonChangelog(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String genText() {
        return "Changelog";
    }

    @Override
    public BoundingBox3d genBound() {
        buttonBound.set(game.aspectratio/2+0.025f, .4f, -.05f,game.aspectratio-.25f , .5f, 0);
        return buttonBound;
    }

    @Override
    public boolean click(Finger finger) {
        game.gui.openMenu(new GuiChangelog(game));
        return true;
    }

}
