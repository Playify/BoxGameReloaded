package at.playify.boxgamereloaded.gui.button.overlay;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class HoveringPlayIcon extends Button {
    private float pauseState;
    private final float[][] vertex=new float[8][9];

    {
        float v = 0.03f;
        float[] verts = {
                -1, -1, v,
                -1, 1, v,
                1, 0, v
        };
        int i=0;
        System.arraycopy(verts,0,vertex[i++],0,9);
        //bottom
        verts[4] = -1;
        verts[5] = -v;
        System.arraycopy(verts,0,vertex[i++],0,9);
        verts[0] = 1;
        verts[1] = 0;
        verts[2] = -v;
        System.arraycopy(verts,0,vertex[i++],0,9);
        //top
        verts[3] = -1;
        verts[4] = 1;
        verts[5] = -v;
        System.arraycopy(verts,0,vertex[i++],0,9);
        verts[0] = -1;
        verts[1] = 1;
        verts[2] = v;
        System.arraycopy(verts,0,vertex[i++],0,9);
        //wall
        verts[6] = -1;
        verts[7] = -1;
        verts[8] = v;
        System.arraycopy(verts,0,vertex[i++],0,9);
        verts[0] = -1;
        verts[1] = -1;
        verts[2] = -v;
        System.arraycopy(verts,0,vertex[i++],0,9);
        verts[6] = 1;
        verts[7] = 0;
        verts[8] = -v;
        System.arraycopy(verts,0,vertex[i],0,9);
    }


    public HoveringPlayIcon(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String genText() {
        return "PlayIcon";
    }

    @Override
    public BoundingBox3d genBound() {
        if(pauseState!=0&&!game.gui.isMainMenuVisible()&&!game.gui.isOptionsVisible()) {
            float v=(game.aspectratio-1+.1f)/2;
            buttonBound.set(v, .1f, -.5f, game.aspectratio-v, 1, -1.5f);
        }else{
            buttonBound.set(0,0,0,0,0,0);
        }
        return buttonBound;
    }

    @Override
    public void draw(Drawer d) {
        if (pauseState!=0&&!game.gui.isMainMenuVisible()&&!game.gui.isOptionsVisible()) {
            d.pushMatrix();
            d.translate(.5f, .23f);
            d.depth(false);
            if (game.connection.userpause) {
                d.drawStringCenter("Paused", 0, -.4f + pauseState * .4f, .15f, 0x66000000);
                if (pauseState == 1 && game.connection.pauseCount != 0) {
                    d.drawStringCenter("by " + game.connection.pauseCount + " Players", 0, -.05f, .05f, 0x66000000);
                }
            }
            d.depth(true);
            d.translate(0, .4f, -.3f);

            d.scale(pauseState);

            long angle = (System.currentTimeMillis() / 20) % 720;
            d.rotate(angle, 0, 1, 0);
            d.translate(0, ((float) Math.sin(Math.PI/180*(angle * 2))) * .04f);
            d.scale(.15f, .15f);
            int i=0;
            d.vertex(vertex[i++], 0xFF005C7A, 1);
            d.vertex(vertex[i++], 0xFF005C7A, 0.8f);
            d.vertex(vertex[i++], 0xFF005C7A, 0.8f);
            d.vertex(vertex[i++], 0xFF005C7A, 0.9f);
            d.vertex(vertex[i++], 0xFF005C7A, 0.9f);
            d.vertex(vertex[i++], 0xFF005C7A, 0.95f);
            d.vertex(vertex[i++], 0xFF005C7A, 0.95f);
            d.vertex(vertex[i], 0xFF005C7A, 1);
            d.popMatrix();
        }
    }

    @Override
    public boolean click(Finger finger) {
        return false;
    }

    @Override
    public boolean tick() {
        if ((game.paused||(game.connection==null||game.connection.pauseCount>0))&&!game.gui.isOptionsVisible()) {
            pauseState = Math.min(1f, pauseState + 1 / 8f);
        } else {
            pauseState = Math.max(0, pauseState - 1 / 8f);
        }
        return pauseState == 0 || pauseState == 1;
    }
}
