package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.*;
import at.playify.boxgamereloaded.gui.button.options.ConsoleButton;
import at.playify.boxgamereloaded.gui.button.paint.*;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiOptionsPaint extends GuiOptions {
    public int variant;

    GuiOptionsPaint(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ConsoleButton(game));
        buttons.add(new Button(game) {
            @Override
            public String genText() {
                return "Paint Menu";
            }

            @Override
            public BoundingBox3d genBound() {
                float v=.2f;
                buttonBound.set(v, v, 0, game.aspectratio-v, 1-v, .1f);
                return buttonBound;
            }

            @Override
            public void draw(Drawer d) {
                d.pushMatrix();
                BoundingBox3d bound=genBound();
                d.cube(0, 0, 0, 1, 1, 1, genColor());
                float v=(bound.maxY-bound.minY);
                float v2=(bound.maxX-bound.minX);
                d.scale(1/v2, 1/v,1);
                d.drawStringCenter(genText(), v2/2, v*6/7, v/8,0x66000000);
                d.popMatrix();
            }

            @Override
            public int genColor() {
                return 0xFF007599;
            }

            @Override
            public boolean click(Finger finger) {
                return true;
            }
        });
        buttons.add(new ButtonClipBoard(game,false));
        buttons.add(new ButtonClipBoard(game,true));
        buttons.add(new ButtonUpload(game));
    }
}
