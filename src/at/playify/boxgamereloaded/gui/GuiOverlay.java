package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.*;
import at.playify.boxgamereloaded.gui.button.overlay.*;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class GuiOverlay extends Gui {
    private ArrayList<Gui> guis = new ArrayList<>();
    public GuiDraw drawer;
    public GuiMainMenu main;
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
        buttons.add(new KeyButtons(game));
        buttons.add(new ButtonButtons(game));
    }

    public void openMainMenu() {
        main=new GuiMainMenu(game);
        game.painter.zoom=game.painter.quick=game.painter.draw=false;
    }

    public boolean isMainMenuVisible() {
        return main!=null;
    }

    @SuppressWarnings("unused")
    public void openMenu(Gui g) {
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
            if (finger.x<width/4) {
                game.cheatCode('l');
            } else if (finger.x<width/2) {
                game.cheatCode('r');
            }
        }
        return click;
    }

    @Override
    public void draw() {
        super.draw();
        if (main!=null) {
            main.draw();
            game.d.clearDepth();
        }
        if (options!=null) options.draw();
        if (drawer!=null) drawer.draw();
        int size=guis.size();
        for (int i=size-1;i >= 0;i--) {
            Gui gui=guis.get(i);
            gui.draw();
        }
    }

    @Override
    public boolean tick() {
        if (options!=null){
            boolean draw = game.painter.draw;
            if (draw!=(options instanceof GuiOptionsPaint)) {
                options=draw?new GuiOptionsPaint(game):new GuiOptions(game);
            }
        }
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

    public void close(Gui gui) {
        guis.remove(gui);
    }

    @Override
    public boolean key(char c, boolean down) {
        boolean done = false;
        int size=guis.size();
        for (int i=0;i<size;i++) {
            Gui gui=guis.get(i);
            if (gui.key(c,down)) {
                done=true;
                break;
            }
        }
        if (!done) {
            done=drawer.key(c,down);
        }
        if (!done) {
            done = super.key(c,down);
        }
        if (!done&&options!=null) {
            done=options.key(c,down);
        }
        if (!done&&main!=null) {
            done=main.key(c,down);
        }
        return done;
    }
    @Override
    public boolean scroll(float f) {
        boolean scrolled = false;
        int size=guis.size();
        for (int i=0;i<size;i++) {
            Gui gui=guis.get(i);
            if (gui.scroll(f)) {
                scrolled=true;
                break;
            }
        }
        if (!scrolled) {
            scrolled=drawer.scroll(f);
        }
        if (!scrolled) {
            scrolled = super.scroll(f);
        }
        if (!scrolled&&options!=null) {
            scrolled=options.scroll(f);
        }
        if (!scrolled&&main!=null) {
            scrolled=main.scroll(f);
        }
        return scrolled;
    }
}
