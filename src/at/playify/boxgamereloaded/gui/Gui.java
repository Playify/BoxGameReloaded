package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Gui {
    protected final BoxGameReloaded game;
    protected ArrayList<Button> buttons = new ArrayList<>();

    public Gui(BoxGameReloaded game) {
        this.game = game;
        initGui();
        Collections.sort(buttons);
    }

    public abstract void initGui();

    public void fingerStateChanged(Finger finger) {

    }

    public void draw() {
        Drawer d = game.d;
        for (Button button : buttons) {
            d.pushMatrix();
            BoundingBox3d bound = button.bound();
            d.translate(bound.minX, bound.minY, bound.minZ);
            d.scale(bound.maxX - bound.minX, bound.maxY - bound.minY, bound.maxZ - bound.minZ);
            button.draw(d);
            d.popMatrix();
        }
    }

    public boolean click(Finger finger) {
        float x = finger.x / game.d.getHeight(), y = 1 - finger.y / game.d.getHeight();
        for (Button button : buttons) {
            BoundingBox3d bound = button.bound();
            if (bound.contains(x, y)) {
                if (button.click(finger)) {
                    return true;
                }
            }
        }
        return false;
    }
}
