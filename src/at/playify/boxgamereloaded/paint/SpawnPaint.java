package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;

public class SpawnPaint implements Paintable {
    private final BoxGameReloaded game;

    SpawnPaint(BoxGameReloaded game) {
        this.game=game;
    }

    @Override
    public void draw(int data) {
        game.d.pushMatrix();
        game.d.translate(.5f, .5f, .5f);
        final int v=50;
        float angle=System.currentTimeMillis()%(360*v)/(float) v;
        game.d.rotate(angle, 0, 1, 0);
        game.d.lineCube(-.5f, -.5f, -.5f, 1, 1, 1, 0xFFFF0000);
        game.d.popMatrix();
    }

    @Override
    public String name(int data) {
        return "Spawn";
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
        int bx=(int) x, by=(int) y;
        game.level.spawnPoint.set(bx+.1f, by, .8f, .8f);
    }
}
