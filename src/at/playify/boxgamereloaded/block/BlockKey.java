package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockKey extends Block implements Collideable {
    public static final char chr='q';
    private boolean collided;
    private float[] vertex3d=new float[]{
            -1, 1, 0,
            0, 0, -0.25f,
            0, .7f, 0,
            1, 1, 0,
            0, 0, -0.25f,
            0, .7f, 0
    };
    private float[] vertex=new float[]{
            -1, 1, 0,
            0, 0, 0,
            0, .7f, 0,
            1, 1, 0,
            0, 0, 0,
            0, .7f, 0
    };

    BlockKey(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
    }

    @Override
    protected boolean isBackGround(int meta) {
        return true;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return checkOnly&&b.collide(bound.set(x, y, 1, 1));
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.pushMatrix();
            int color=Utils.hsvToRgb(level.getMeta(x, y)/(float) metaStates());
            game.d.translate(x+.5f, y+.5f, .5f);
            game.d.scale(1/10f);
            final int v=50;
            float wingel=System.currentTimeMillis()%(360*v)/(float) v;
            game.d.rotate(wingel, 0, 1, 0);

            game.d.rotate(45, 0, 0, 1);
            game.d.translate(-4, -1.5f, 0);
            game.d.cube(0, 0, 0, 1, 1, 1, color, false, true, true, true, true, true);
            game.d.cube(2, 0, 0, 1, 1, 1, color, false, true, true, true, true, true);
            game.d.cube(0, 1, 0, 6, 1, 1, color, true, true, true, true, true, true);
            game.d.cube(5, 0, 0, 3, 1, 1, color, true, true, true, true, true, true);
            game.d.cube(7, 1, 0, 1, 1, 1, color, false, true, true, true, true, true);
            game.d.cube(5, 2, 0, 3, 1, 1, color, true, true, true, true, true, true);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            int color=Utils.hsvToRgb(level.getMeta(x, y)/(float) metaStates());
            game.d.translate(x+.5f, y+.5f, 0);
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

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        game.vars.keys[meta]=true;
        return true;
    }

    @Override
    public int metaStates() {
        return 8;
    }
}