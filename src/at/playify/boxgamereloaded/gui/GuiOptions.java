package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.ConsoleButton;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiOptions extends Gui {

    public GuiOptions(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ConsoleButton(game));
        buttons.add(new Button(game) {
            @Override
            public String text() {
                return "";
            }

            @Override
            public BoundingBox3d bound() {
                float v=.2f;
                bound.set(v, v, 0, game.aspectratio-v, 1-v, 0);
                return bound;
            }

            @Override
            public boolean click(Finger finger) {
                return true;
            }
        });
    }
}
