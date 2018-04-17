package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.LevelButton;

public class GuiMainMenu extends Gui {
    public GuiMainMenu(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui() {
        buttons.add(new LevelButton(game, "paint_0", 0.1f, 0.1f));
    }
}
