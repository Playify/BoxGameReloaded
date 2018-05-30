package at.playify.boxgamereloaded.gui.button.overlay;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
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
        float aspect=game.d.getWidth()/game.d.getHeight();
        bound.set(aspect-1/6f-1/5f, 5/6f, 0, aspect-1/5f, 1, .025f);
        return bound;
    }

    @Override
    public void draw(Drawer d) {
        if (visibleState==0) return;
        if (game.gui.isMainMenuVisible()) return;
        int color=color();
        d.translate(0.5f, 0, 0);
        float z=(bound.maxX-bound.minX)/(bound.maxZ-bound.minZ);
        d.scale(1, 1, z);
        float zz=(bound.maxZ-bound.minZ)*2;
        d.translate(0, 0, zz);

        d.translate(0, 1, 0);
        d.scale(visibleState, visibleState, visibleState);
        d.translate(0, -.5f, 0);
        d.rotate((float) Math.sin(Math.toRadians((1-visibleState)*500))*10f, 0, 0, 1);
        d.translate(0, -.5f, 0);

        d.rotate(180*mainState, 0, 1, 0);
        d.translate(0, 0, -zz);
        d.scale(1, 1, 1/z);
        d.cube(-1/2f, 0, 0, 1, 1/7f, 1, color, true, true, true, true, true, true);
        d.cube(-1/2f, 6/7f, 0, 1, 1/7f, 1, color, true, true, true, true, true, true);
        d.cube(-1/2f, 1/7f, 0, 1/7f, 5/7f, 1, color, false, true, false, true, true, true);
        d.cube(5/14f, 1/7f, 0, 1/7f, 5/7f, 1, color, false, true, false, true, true, true);
        if (mainState>150/255f) {
            d.cube(-1f/2+2f/5, 1f/5-1/7f, 0, 1f/5, 1f/5+1/7f, 1, color, true, true, false, true, game.d.back(), true);
            d.cube(-1f/2+2f/5, 3f/5, 0, 1f/5, 1f/5+1/7f, 1, color, false, true, true, true, game.d.back(), true);
        } else {
            if (game.painter.pause()&&game.level.hasHistory()) {
                d.pushMatrix();
                d.translate(0, .5f, 1/2f);
                d.scale(1/2f);
                float v=1.75f;
                d.rect(-v/2f, -v/2f, v, v, 0xA0000000);
                d.drawStringCenter("UN", 0, 0, .5f, 0xFF00FF00);
                d.drawStringCenter("DO", 0, -.5f, .5f, 0xFF00FF00);
                d.popMatrix();
            } else {
                float v=0.1f;
                d.translate(0, 1/2f, 0);
                d.rotate(45, 0, 0, 1);
                float a=.15f, b=.3f;
                for (int i=0;i<4;i++) {
                    d.rotate(90, 0, 0, 1);
                    d.cube(v, v, 0, b, a, 1, color, true, true, true, true);
                    d.cube(v, v, 0, a, b, 1, color, true, true, true, true);
                }
                d.rotate(-45, 0, 0, 1);
            }
        }
    }

    @Override
    public boolean click(Finger finger) {
        if (game.gui.isMainMenuVisible()) return false;
        if (visibleState==1) {
            if (mainState>150/255f) {
                game.connection.leaveWorld();
                game.gui.openMainMenu();
            } else {
                if (game.painter.pause()) game.level.loadHistory();
                else game.player.killedByButton();
            }
            return true;
        } else return false;
    }

    @Override
    public boolean tick() {
        boolean visible=!game.gui.isOptionsVisible()&&!game.gui.isMainMenuVisible()&&!(game.painter.quick&&!game.gui.isOptionsVisible());
        boolean main=game.gui.isMainMenuVisible()||game.paused||game.connection==null||game.connection.pauseCount>0;
        if (game.painter.pause()){
            visible=main?visible:game.level.hasHistory();
        }
        if (visible) {
            visibleState=Math.min(1f, visibleState+1/8f);
        } else {
            visibleState=Math.max(0, visibleState-1/8f);
        }
        if (main) {
            mainState=Math.min(1f, mainState+1/8f);
        } else {
            mainState=Math.max(0, mainState-1/8f);
        }
        return (mainState==0||mainState==1)&&(visibleState==0||visibleState==1);
    }
}
