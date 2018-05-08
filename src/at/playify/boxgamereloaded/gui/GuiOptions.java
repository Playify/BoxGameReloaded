package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.ButtonClipBoard;
import at.playify.boxgamereloaded.gui.button.ConsoleButton;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiOptions extends Gui {

    GuiOptions(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ConsoleButton(game));
        buttons.add(new Button(game) {
            @Override
            public String text() {
                return "Options Menu, Coming Soon";
            }

            @Override
            public BoundingBox3d bound() {
                float v=.2f;
                bound.set(v, v, 0, game.aspectratio-v, 1-v, 0);
                return bound;
            }

            @Override
            public void draw(Drawer d) {
                d.pushMatrix();
                BoundingBox3d bound=bound();
                d.cube(0, 0, 0, 1, 1, 1, color());
                float v=(bound.maxY-bound.minY);
                d.scale(1, 1/v,1);
                d.drawStringCenter("Options Menu", .5f, v*6/7, v/8);
                d.drawStringCenter("Coming Soon", .5f, v*5/7, v/8);
                d.popMatrix();
            }

            @Override
            public int color() {
                return 0xFF007599;
            }

            @Override
            public boolean click(Finger finger) {
                return true;
            }
        });
        buttons.add(new ButtonClipBoard(game,false));
        buttons.add(new ButtonClipBoard(game,true));
    }
}
