package at.playify.boxgamereloaded.gui.button.overlay;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class OptionsButton extends Button {
    private int settingsRotate;

    public OptionsButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Options";
    }

    @Override
    public BoundingBox3d bound() {
        float s = 1 / 6f;
        bound.set(0, 1 - s, 0, s, 1, .025f);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        if (game.gui.isMainMenuVisible()&&game.gui.main.uiState==0)return;
        d.pushMatrix();
        float visibleState=game.gui.main==null?1:game.gui.main.uiState;
        d.translate(0.5f, 0, 0);
        float z=(bound.maxX-bound.minX)/(bound.maxZ-bound.minZ);
        d.scale(1, 1, z);
        float zz=(bound.maxZ-bound.minZ)*2;
        d.translate(0, 0, zz);

        d.translate(0, 1, 0);
        d.scale(visibleState, visibleState, visibleState);
        d.translate(0, -.5f, 0);
        d.rotate((float) Math.sin(Math.toRadians((1-visibleState)*500))*10f, 0, 0, 1);
        d.translate(-.5f, -.5f, 0);
        d.translate(0, 0, -zz);
        d.scale(1, 1, 1/z);
        int color = color();
        d.cube(0, 0, 0, 1, 1 / 7f, 1, color, true, false, true, false);
        d.cube(0, 6 / 7f, 0, 1, 1 / 7f, 1, color, true, false, true, false);
        d.cube(0, 0, 0, 1 / 7f, 1, 1, color, false, true, false, true);
        d.cube(6 / 7f, 0, 0, 1 / 7f, 1, 1, color, false, true, false, true);
        d.translate(1 / 2f, 1 / 2f);
        d.rotate(-settingsRotate, 0, 0, 1);
        for (int i = 0; i < 4; i++) {
            d.cube(-3 / 14f, -3 / 14f, 0, 1 / 7f, 3 / 7f, 1, color, false, true, false, true);
            d.rotate(90, 0, 0, 1);
        }
        d.popMatrix();
    }

    @Override
    public boolean click(Finger finger) {
        game.cheatCode('a');
        if (game.gui.isMainMenuVisible()&&game.gui.main.uiState!=1)return false;
        if (game.gui.isOptionsVisible()) {
            game.gui.closeOptions();
        } else {
            game.gui.openOptions();
        }
        return true;
    }

    @Override
    public boolean tick() {
        int v = 5;
        if (settingsRotate > 0) {
            settingsRotate += v;
            if (settingsRotate >= 90) settingsRotate = 0;
        } else if (game.keys['o']) {
            settingsRotate = v;
        } else {
            BoundingBox3d bound = bound();
            for (Finger finger : game.fingers) {
                float x=finger.x/game.d.getHeight(), y=1-finger.y/game.d.getHeight();
                if (finger.control && bound.contains(x, y)) {
                    settingsRotate = v;
                    break;
                }
            }
        }
        return settingsRotate == 0;
    }
}
