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
        for (Button button : buttons) {
            d.pushMatrix();
            BoundingBox3d bound=button.bound();
            d.translate(bound.minX, bound.minY, bound.minZ);
            d.scale(bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ);
            button.draw(d);
            d.popMatrix();
        }
    }

    public boolean click(Finger finger) {
        float x=finger.getX()/game.d.getHeight(), y=1-finger.getY()/game.d.getHeight();
        for (Button button : buttons) {
            BoundingBox3d bound=button.bound();
            if (bound.contains(x, y)) {
                if (button.click(finger)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean tick() {
        boolean freeze = true;
        for (Button button : buttons) {
            freeze&=button.tick();
        }
        return freeze;
    }
}
