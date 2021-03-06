package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("WeakerAccess")
public abstract class Gui {
    protected final BoxGameReloaded game;
    protected final Button[] buttons;

    public Gui(BoxGameReloaded game) {
        this.game = game;
        ArrayList<Button> buttons=new ArrayList<>();
        initGui(buttons);
        Collections.sort(buttons);
        this.buttons=buttons.toArray(new Button[0]);
    }

    public abstract void initGui(ArrayList<Button> buttons);

    public void draw() {
        Drawer d = game.d;
        for (int i=buttons.length-1;i >= 0;i--) {
            Button button=buttons[i];
            d.pushMatrix();
            BoundingBox3d bound=button.genBound();
            if (!bound.isEmpty()) {
                d.translate(bound.minX, bound.minY, bound.minZ);
                d.scale(bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ);
                button.draw(d);
            }
            d.popMatrix();
        }
        d.clearDepth();
    }

    public boolean click(Finger finger) {
        float x=finger.x/game.d.getHeight(), y=1-finger.y/game.d.getHeight();
        for (Button button : buttons) {
            BoundingBox3d bound=button.genBound();
            if (bound.contains(x, y)) {
                if (button.click(finger)) {
                    button.onClick();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean key(char c,boolean down) {
        return false;
    }

    public boolean tick() {
        boolean freeze = true;
        for (Button button : buttons) {
            freeze&=button.tick();
        }
        return freeze;
    }

    public boolean scroll(float f) {
        return false;
    }

    public boolean clickButtons(Finger finger) {
        float x=finger.x/game.d.getHeight(), y=1-finger.y/game.d.getHeight();
        for (Button button : buttons) {
            BoundingBox3d bound=button.genBound();
            if (bound.contains(x, y)) {
                if (button.click(finger)) {
                    button.onClick();
                    return true;
                }
            }
        }
        return false;
    }
}
