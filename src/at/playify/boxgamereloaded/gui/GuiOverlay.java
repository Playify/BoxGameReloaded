package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.*;

public class GuiOverlay extends Gui {

    public GuiOverlay(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui() {
        buttons.add(new DarkBackground(game));
        buttons.add(new HoveringPlayIcon(game));
        buttons.add(new OptionsButton(game));
        buttons.add(new PauseButton(game));
        buttons.add(new RespawnButton(game));
    }

    public void openMainMenu() {

    }

    public boolean isMainMenuVisible() {
        return false;
    }
}
