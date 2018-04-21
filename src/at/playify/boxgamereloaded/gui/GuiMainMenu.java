package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.LevelButton;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiMainMenu extends Gui {
    public GuiMainMenu(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        for (int i = 0; i < 12; i++) {
            float x = (i % 4) / 10f;
            float y = (2 - i / 4) / 10f;
            buttons.add(new LevelButton(game, "paint_" + i, .2f + x * 4, .1f + y * 3));
        }
    }

    @Override
    public void draw() {
        final float v=1;
        game.d.cube(-v, -v, 1, game.aspectratio+2*v, 1+2*v, 0, 0xFF000000, false, false, false, false, true, false);
        super.draw();
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
