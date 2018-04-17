package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.LevelButton;
import at.playify.boxgamereloaded.util.Finger;

public class GuiMainMenu extends Gui {
    public GuiMainMenu(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui() {
        for (int i = 0; i < 12; i++) {
            float x = (i % 4) / 10f;
            float y = (2 - i / 4) / 10f;
            buttons.add(new LevelButton(game, "paint_" + i, .2f + x * 4, .1f + y * 3));
        }
    }

    @Override
    public boolean click(Finger finger) {
        super.click(finger);
        return true;
    }

    @Override
    public boolean tick() {
        game.paused = true;
        return super.tick();
    }
}
