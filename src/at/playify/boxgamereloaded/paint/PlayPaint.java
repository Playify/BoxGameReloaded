package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;

public class PlayPaint implements Paintable {
    private final float[][] vertex=new float[8][9];
    private BoxGameReloaded game;

    {
        float v = 0.3f;
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

    public PlayPaint(BoxGameReloaded game) {
        this.game=game;
    }

    @Override
    public void draw(int data) {
        game.d.pushMatrix();
        game.d.translate(.5f, .5f, .5f);
        if (game.vars.cubic) {
            final int v=50;
            float angle=System.currentTimeMillis()%(360*v)/(float) v;
            game.d.rotate(angle, 0, 1, 0);
            int i=0;
            game.d.translate(0, ((float) Math.sin(Math.toRadians(angle * 2))) * .2f);
            game.d.scale(.5f);
            game.d.vertex(vertex[i++], 0xFF005C7A, 1);
            game.d.vertex(vertex[i++], 0xFF005C7A, 0.8f);
            game.d.vertex(vertex[i++], 0xFF005C7A, 0.8f);
            game.d.vertex(vertex[i++], 0xFF005C7A, 0.9f);
            game.d.vertex(vertex[i++], 0xFF005C7A, 0.9f);
            game.d.vertex(vertex[i++], 0xFF005C7A, 0.95f);
            game.d.vertex(vertex[i++], 0xFF005C7A, 0.95f);
            game.d.vertex(vertex[i], 0xFF005C7A, 1);
        }else{
            game.d.scale(.5f);
            game.d.vertex(vertex[0],0xFF005C7A, 1);
        }
        game.d.popMatrix();
    }

    @Override
    public String name(int data) {
        return "Play";
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public boolean history() {
        return false;
    }
}
