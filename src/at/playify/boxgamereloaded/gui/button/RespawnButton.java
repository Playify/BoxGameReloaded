package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class RespawnButton extends Button {

    private float visibleState;
    private float mainState;

    public RespawnButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Respawn";
    }

    @Override
    public BoundingBox3d bound() {
        float aspect = game.d.getWidth() / game.d.getHeight();
        bound.set(aspect - 1 / 6f - 1 / 5f, 5 / 6f, 0, aspect - 1 / 5f, 1, .025f);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        int color = color();
        d.translate(0.5f, 0, 0);
        float z = (bound.maxX - bound.minX) / (bound.maxZ - bound.minZ);
        d.scale(1, 1, z);
        float zz = (bound.maxZ - bound.minZ) * 2;
        d.translate(0, 0, zz);
        d.rotate(180 * mainState, 0, 1, 0);
        d.translate(0, 0, -zz);
        d.scale(1, 1, 1 / z);
        if (mainState <= 150 / 255f) {
            //d.cube(-1/2f+1/14f, 1/14f,1, 6/7f, 6/7f,1,0xB2FF0000,false,false,false,false,true,false);
        }
        d.cube(-1 / 2f, 0, 0, 1, 1 / 7f, 1, color, true, true, true, true, true, true);
        d.cube(-1 / 2f, 6 / 7f, 0, 1, 1 / 7f, 1, color, true, true, true, true, true, true);
        d.cube(-1 / 2f, 1 / 7f, 0, 1 / 7f, 5 / 7f, 1, color, false, true, false, true, true, true);
        d.cube(5 / 14f, 1 / 7f, 0, 1 / 7f, 5 / 7f, 1, color, false, true, false, true, true, true);
        if (mainState > 150 / 255f) {
            d.cube(-1f / 2 + 2f / 5, 1f / 5 - 1 / 7f, 0, 1f / 5, 1f / 5 + 1 / 7f, 1, color, true, true, false, true, game.d.back(), true);
            d.cube(-1f / 2 + 2f / 5, 3f / 5, 0, 1f / 5, 1f / 5 + 1 / 7f, 1, color, false, true, true, true, game.d.back(), true);
        } else {
            float v = 0.1f;
            d.translate(0, 1 / 2f, 0);
            d.rotate(45, 0, 0, 1);
            float a = .15f, b = .3f;
            for (int i = 0; i < 4; i++) {
                d.rotate(90, 0, 0, 1);
                d.cube(v, v, 0, b, a, 1, color, true, true, true, true);
                d.cube(v, v, 0, a, b, 1, color, true, true, true, true);
            }
            d.rotate(-45, 0, 0, 1);
        }
    }

    @Override
    public boolean click(Finger finger) {
        if (mainState > 150 / 255f) {
            game.connection.leaveWorld();
            game.gui.openMainMenu();
        } else {
            game.player.killedByButton();
        }
        return true;
    }

    @Override
    public boolean tick() {
        boolean visible = !game.options && !game.gui.isMainMenuVisible();
        boolean main = game.gui.isMainMenuVisible() || game.paused || (game.connection == null || game.connection.isPaused(true));
        if (visible) {
            visibleState = Math.min(1f, visibleState + 1 / 8f);
        } else {
            visibleState = Math.max(0, visibleState - 1 / 8f);
        }
        if (main) {
            mainState = Math.min(1f, mainState + 1 / 8f);
        } else {
            mainState = Math.max(0, mainState - 1 / 8f);
        }
        return (mainState == 0 || mainState == 1) && (visibleState == 0 || visibleState == 1);
    }
}
