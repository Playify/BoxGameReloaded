package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.ButtonConnectGui;
import at.playify.boxgamereloaded.gui.button.LevelButton;
import at.playify.boxgamereloaded.gui.button.StageSelector;
import at.playify.boxgamereloaded.network.packet.PacketMainMenu;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiMainMenu extends Gui {
    public float uiState;
    public boolean ui;
    public Scroller scroller;

    GuiMainMenu(BoxGameReloaded game) {
        super(game);
        if (game.connection!=null) {
            game.connection.sendPacket(new PacketMainMenu());
        }
    }

    @Override
    public void initGui(final ArrayList<Button> buttons) {
        this.scroller=new Scroller(game,this);
        for (int i=0;i<7;i++) {
            buttons.add(new LevelButton(game, i, this));
        }
        buttons.add(new StageSelector(game, this));
        buttons.add(new ButtonConnectGui(game));
    }

    @Override
    public boolean clickButtons(Finger finger) {
        if (!super.clickButtons(finger)) {
            ui^=true;
            return false;
        }
        return true;
    }

    @Override
    public void draw() {
        int index=game.levels.containsKey(game.vars.stage)? game.levels.get(game.vars.stage).size():0;
        scroller.scrollMax=(index*(.2f+.05f))-.225f;
        scroller.draw();
        super.draw();
    }

    @Override
    public boolean click(Finger finger) {
        return scroller.click(finger);
    }

    @Override
    public boolean tick() {
        scroller.tick();
        game.paused=true;
        if (ui) {
            uiState= Math.min(1f, uiState+ 1 / 8f);
        } else {
            uiState= Math.max(0, uiState- 1 / 8f);
        }
        super.tick();
        return false;
    }

    @Override
    public boolean scroll(float f) {
        scroller.scroll(f);
        return true;
    }
}
