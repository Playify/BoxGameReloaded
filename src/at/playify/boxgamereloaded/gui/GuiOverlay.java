package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.*;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;
import java.util.Iterator;

public class GuiOverlay extends Gui {
    private ArrayList<Gui> guis = new ArrayList<>();

    public GuiOverlay(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui() {
        buttons.add(new DarkBackground(game));
        buttons.add(new HoveringPlayIcon(game));
        buttons.add(new OptionsButton(game));
        buttons.add(new PauseButton(game));
        buttons.add(new RespawnButton(game));
    }

    public void openMainMenu() {
        if (!isMainMenuVisible()) {
            guis.add(new GuiMainMenu(game));
        }
    }

    public boolean isMainMenuVisible() {
        for (Gui gui : guis) {
            if (gui instanceof GuiMainMenu) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean click(Finger finger) {
        boolean click = false;
        for (Gui gui : guis) {
            if (gui.click(finger)) {
                click = true;
                break;
            }
        }
        if (!click) {
            click = super.click(finger);
        }
        if (!click) {
            float width = game.d.getWidth();
            if (finger.x < width / 4) {
                game.cheatCode('l');
            } else if (finger.x < width / 2) {
                game.cheatCode('r');
            }
        }
        return click;
    }

    @Override
    public void draw() {
        for (Gui gui : guis) {
            gui.draw();
        }
        super.draw();

    }

    @Override
    public boolean tick() {
        boolean freeze = true;
        for (Gui gui : guis) {
            freeze &= gui.tick();
        }
        return freeze && super.tick();
    }

    public void closeMainMenu() {
        Iterator<Gui> iterator = guis.iterator();
        while (iterator.hasNext()) {
            Gui next = iterator.next();
            if (next instanceof GuiMainMenu) {
                iterator.remove();
            }
        }
    }
}
