package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.PaintButton;
import at.playify.boxgamereloaded.gui.button.PaintSelectButton;
import at.playify.boxgamereloaded.paint.Paintable;

import java.util.ArrayList;

public class GuiDraw extends Gui {
    public boolean quick;
    public float state;

    GuiDraw(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new PaintButton(game));
        ArrayList<Paintable> p=game.painter.list;
        for (int i=1;i<49;i++) {
            buttons.add(new PaintSelectButton(game, p, i, this));
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (!game.painter.draw||quick) return;
        String name=game.painter.paint().name(0);
        game.d.drawString(name, game.aspectratio-1/7f-0.01f-game.d.getStringWidth(name)*0.05f, 0.01f, 0.05f);
    }

    @Override
    public boolean tick() {
        if (!game.gui.isOptionsVisible()&&game.gui.drawer.quick&&game.painter.draw) {
            state=Math.min(1f, state+1/4f);
        } else {
            state=Math.max(0, state-1/4f);
        }
        return super.tick()&&(state==0||state==1);
    }
}
