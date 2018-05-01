package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.*;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class GuiOverlay extends Gui {
    private ArrayList<Gui> guis = new ArrayList<>();
    public GuiDraw drawer;
    private GuiMainMenu main;
    private GuiOptions options;

    public GuiOverlay(BoxGameReloaded game) {
        super(game);
        drawer=new GuiDraw(this.game);
        main=new GuiMainMenu(this.game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new DarkBackground(game));
        buttons.add(new HoveringPlayIcon(game));
        buttons.add(new OptionsButton(game));
        buttons.add(new PauseButton(game));
        buttons.add(new RespawnButton(game));
    }

    public void openMainMenu() {
        main=new GuiMainMenu(game);
        game.painter.zoom=game.painter.quick=game.painter.draw=false;
    }

    public boolean isMainMenuVisible() {
        return main!=null;
    }

    @SuppressWarnings("unused")
    void openMenu(Gui g) {
        guis.add(g);
    }

    @Override
    public boolean click(Finger finger) {
        boolean click = false;
        int size=guis.size();
        for (int i=0;i<size;i++) {
            Gui gui=guis.get(i);
            if (gui.click(finger)) {
                click=true;
                break;
            }
        }
        if (!click) {
            click=drawer.click(finger);
        }
        if (!click) {
            click = super.click(finger);
        }
        if (!click&&options!=null) {
            click=options.click(finger);
        }
        if (!click&&main!=null) {
            click=main.click(finger);
        }
        if (!click) {
            float width = game.d.getWidth();
            if (finger.getX()<width/4) {
                game.cheatCode('l');
            } else if (finger.getX()<width/2) {
                game.cheatCode('r');
            }
        }
        return click;
    }

    @Override
    public void draw() {
        int size=guis.size();
        for (int i=0;i<size;i++) {
            Gui gui=guis.get(i);
            gui.draw();
        }
        if (options!=null) options.draw();
        if (main!=null) main.draw();
        if (drawer!=null) drawer.draw();
        super.draw();

    }

    @Override
    public boolean tick() {
        boolean freeze = true;
        int size=guis.size();
        for (int i=0;i<size;i++) {
            Gui gui=guis.get(i);
            freeze&=gui.tick();
        }
        if (options!=null&&!options.tick()) freeze=false;
        if (main!=null&&!main.tick()) freeze=false;
        if (drawer!=null&&!drawer.tick()) freeze=false;
        return freeze && super.tick();
    }

    public void closeMainMenu() {
        main=null;
    }

    public int backgroundState() {
        for (int i=buttons.length-1;i >= 0;i--) {
            Button button=buttons[i];
            if (button instanceof DarkBackground) {
                return ((DarkBackground) button).backgroundState;
            }
        }
        return 0;
    }

    public void openOptions() {
        options=new GuiOptions(game);
        game.painter.quick=false;
    }

    public boolean isOptionsVisible() {
        return options!=null;
    }

    public void closeOptions() {
        options=null;
    }
}
