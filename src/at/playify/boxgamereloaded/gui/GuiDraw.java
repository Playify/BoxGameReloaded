package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.PaintButton;
import at.playify.boxgamereloaded.gui.button.PaintSelectButton;

import java.util.ArrayList;

public class GuiDraw extends Gui {
    public boolean quick;
    public float state;

    public GuiDraw(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new PaintButton(game));
        for (int i=1;i<49;i++) {
            buttons.add(new PaintSelectButton(game, i, this));
        }
    }

    @Override
    public boolean tick() {
        if (!game.gui.isOptionsVisible()&&game.gui.drawer.quick&&game.painter.draw) {
            state=Math.min(1f, state+1/8f);
        } else {
            state=Math.max(0, state-1/8f);
        }
        return super.tick()&&(state==0||state==1);
    }
}
