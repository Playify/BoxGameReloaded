package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class KeyButtons extends Button {
    public KeyButtons(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "KeyButtons";
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(0,6/7f,-1/14f,1/7f,1,1/14f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        return false;
    }

    @Override
    public void draw(Drawer d) {
        d.translate(.75f,0,0);
        for (int i=0;i<game.vars.keys.length;i++) {
            if (game.vars.keys[i]){
                d.translate(.75f,0,0);
                game.vertex.drawKey(true,i);
            }
        }
    }
}
