package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class SpawnPaint implements Paintable {
    private final BoxGameReloaded game;
    private RectBound bound=new RectBound();

    SpawnPaint(BoxGameReloaded game) {
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
            game.d.lineCube(-.5f, -.5f, -.5f, 1, 1, 1, 0xFF000000);
        }else{
            game.d.lineRect(-.5f,-.5f,1,1,0xFF000000);
        }
        game.d.popMatrix();
    }

    @Override
    public String name(int data) {
        return "Checkpoint";
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
        int bx=(int) x, by=(int) y;
        bound.set(bx+.1f, by, .8f, .8f);
        game.vars.check.check(bound);
    }

    @Override
    public boolean canDraw() {
        return true;
    }
}
