package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.OptionsButton;
import at.playify.boxgamereloaded.gui.button.PauseButton;

public class GuiOverlay extends Gui {
    public GuiOverlay(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui() {
        buttons.add(new OptionsButton(game));
        buttons.add(new PauseButton(game));
    }

    public void openMainMenu() {

    }
}
