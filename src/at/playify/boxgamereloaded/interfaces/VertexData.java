package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Utils;

public class VertexData {
    private final BoxGameReloaded game;
    public float[] arrow=new float[]{
            0f, -0.3f, 0,
            0f, 0.1f, 0,
            0.3f, 0.3f, 0,
            0f, -0.3f, 0,
            -0f, 0.1f, 0,
            -0.3f, 0.3f, 0
    };
    public float[] bigtriangle=new float[]{
            -.424264f, 0, 0,
            0, -.424264f, 0,
            .424264f, 0, 0
    };
    public float[] button;
    public float[] button2;

    {
        float y=.5f, y0=.1f, y1=1-y0;
        button=new float[]{0, 0, 0, y0, y, 0, 1-y0, y, 0, 0, 0, 0, 1, 0, 0, 1-y0, y, 0};
        button2=new float[]{0, 0, 0, y0, y, 0, 2-y0, y, 0, 0, 0, 0, 2, 0, 0, 2-y0, y, 0};
    }

    public VertexData(BoxGameReloaded game) {
        this.game=game;
    }

    public void drawKey(boolean rotate, int meta) {
        drawKey(rotate, meta, game.vars.cubic);
    }

    public void drawKey(boolean rotate, int meta, boolean cubic) {
        int color=Utils.color(meta, 8);
        if (cubic) {
            game.d.pushMatrix();
            game.d.translate(.5f, .5f, .5f);
            game.d.scale(1/10f);
            if (rotate) {
                final int v=50;
                float wingel=System.currentTimeMillis()%(360*v)/(float) v;
                game.d.rotate(wingel, 0, 1, 0);
            }

            game.d.rotate(45, 0, 0, 1);
            game.d.translate(-4, -1.5f, 0);
            boolean back=rotate||game.d.back();
            game.d.cube(0, 0, 0, 1, 1, 1, color, false, true, true, true, true, back);
            game.d.cube(2, 0, 0, 1, 1, 1, color, false, true, true, true, true, back);
            game.d.cube(0, 1, 0, 6, 1, 1, color, true, true, true, true, true, back);
            game.d.cube(5, 0, 0, 3, 1, 1, color, true, true, true, true, true, back);
            game.d.cube(7, 1, 0, 1, 1, 1, color, false, true, true, true, true, back);
            game.d.cube(5, 2, 0, 3, 1, 1, color, true, true, true, true, true, back);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            game.d.translate(.5f, .5f, 0);
            game.d.scale(1/10f);
            game.d.rotate(45, 0, 0, 1);
            game.d.translate(-4, -1.5f, 0);
            game.d.rect(0, 0, 1, 1, color);
            game.d.rect(2, 0, 1, 1, color);
            game.d.rect(0, 1, 6, 1, color);
            game.d.rect(5, 0, 3, 1, color);
            game.d.rect(7, 1, 1, 1, color);
            game.d.rect(5, 2, 3, 1, color);
            game.d.popMatrix();
        }
    }
}
