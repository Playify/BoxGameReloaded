package at.playify.boxgamereloaded.gui.button.overlay;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class PauseButton extends Button {
    private float visibleState;
    private float pauseState;

    public PauseButton(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String genText() {
        return "Pause";
    }

    @Override
    public BoundingBox3d genBound() {
        float s=1/6f;
        float aspect=game.d.getWidth()/game.d.getHeight();
        buttonBound.set(aspect-s, 1-s, 0, aspect, 1, .025f);
        return buttonBound;
    }

    @Override
    public void draw(Drawer d) {
        if (visibleState==0) return;
        if (game.gui.isMainMenuVisible()) return;
        d.translate(0.5f, 0, 0);
        float z=(buttonBound.maxX-buttonBound.minX)/(buttonBound.maxZ-buttonBound.minZ);
        d.scale(1, 1, z);
        float zz=(buttonBound.maxZ-buttonBound.minZ)*2;
        d.translate(0, 0, zz);

        d.translate(0, 1, 0);
        d.scale(visibleState, visibleState, visibleState);
        d.translate(0, -.5f, 0);
        d.rotate((float) Math.sin(Math.PI/180*((1-visibleState)*500))*10f, 0, 0, 1);
        d.translate(-.5f, -.5f, 0);
        d.translate(0, 0, -zz);
        d.scale(1, 1, 1/z);

        int color=genColor();
        d.cube(0, 0, 0, 1, 1/7f, 1, color, true, true, true, true);
        d.cube(0, 6/7f, 0, 1, 1/7f, 1, color, true, true, pauseState==0, true);
        d.cube(0, 1/7f, 0, 1/7f, 5/7f, 1, color, pauseState==0, true, false, true);
        d.cube(6/7f, 1/7f, 0, 1/7f, 5/7f, 1, color, false, true, false, true);
        if (pauseState==0) {
            d.cube(4/10f, 1/5f-1/7f, 0, 1/5f, 3/5f+2/7f, 1, color, false, true, false, true);
        } else {
            d.translate(1/2f, 1/2f);
            float v=1-.5f*pauseState;
            for (int i=0;i<2;i++) {
                d.pushMatrix();
                d.cube(-1/10f, pauseState/4f, 0, 1/5f, 3/7f*v, 1, color, true, true, true, true);
                d.translate(1/7f-1/2f, 1/7f-1/2f);
                d.rotate(26.565f*pauseState, 0, 0, 1);
                d.cube(0, -1/7f, 0, 6/7f, 1/7f, 1, color, true, false, false, false);
                d.cube(2.8f/7f, -2.3f/7f*pauseState, 0, 2.3f/7f, 1.3f/7f*pauseState, 1, color, false, false, false, false);
                d.popMatrix();
                d.scale(1, -1, 1);
                d.flipCullface();
            }
        }
    }

    @Override
    public boolean click(Finger finger) {
        game.cheatCode('b');
        if (game.gui.isMainMenuVisible()) return false;
        if (visibleState!=1) return false;
        game.paused^=true;
        return true;
    }

    @Override
    public boolean tick() {
        boolean visible=!game.gui.isMainMenuVisible()&&!(game.painter.quick&&!game.gui.isOptionsVisible());
        if (visible) {
            visibleState=Math.min(1f, visibleState+1/8f);
        } else {
            visibleState=Math.max(0, visibleState-1/8f);
            game.paused=false;
        }
        if (game.paused) {
            pauseState=Math.min(1f, pauseState+1/8f);
        } else {
            pauseState=Math.max(0, pauseState-1/8f);
        }
        return pauseState==1&&(visibleState==0||visibleState==1);
    }
}

