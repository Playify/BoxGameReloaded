package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.connect.GuiConnection;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonConnectGui extends Button {
    public ButtonConnectGui(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Multiplayer";
    }

    @Override
    public BoundingBox3d bound() {
        float dy=game.gui==null||game.gui.main==null?0:(1-game.gui.main.uiState)*-.25f;
        bound.set(game.aspectratio/10,.05f+dy,0,game.aspectratio*4/10f,.2f+dy,.05f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        if (game.gui.main.uiState!=1) return false;
        game.gui.openMenu(new GuiConnection(game));
        return true;
    }

    @Override
    public void draw(Drawer d) {
        if (game.gui.main.uiState==0) return;
        super.draw(d);
    }
}
