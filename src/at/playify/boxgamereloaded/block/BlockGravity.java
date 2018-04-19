package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockGravity extends Block implements Collideable, NoCollideable {
    public static char chr='v';
    private boolean collided;
    private float[] vertex=new float[]{
            0f, -0.3f, 0,
            0f, 0.1f, 0,
            0.3f, 0.3f, 0,
            0f, -0.3f, 0,
            -0f, 0.1f, 0,
            -0.3f, 0.3f, 0
    };

    public BlockGravity(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        if (level.getMeta(x, y)==0) {
            list.add(Borrow.bound(x, y, x+1, y+1));
        }
    }

    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        if (meta==0&&checkOnly) {
            final float v=.01f;
            return b.collide(bound.set(x-v, y-v, 1+2*v, 1+2*v));
        } else {
            return b.collide(bound.set(x, y, 1, 1));
        }
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.pushMatrix();
            int meta=level.getMeta(x, y);
            if (meta==0) {
                game.d.cube(x, y, 0f, 1, 1, 1f, 0xFF9c06ad);
                game.d.translate(x+.5f, y+.5f, -.001f);
            } else {
                game.d.cube(x, y, 0.9f, 1, 1, .1f, 0xFFd40adb);
                game.d.translate(x+.5f, y+.5f, .899f);
            }
            if (game.vars.inverted_gravity)
                game.d.scale(1, -1, 1);
            game.d.vertex(vertex, 0xFF0136c6);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            int meta=level.getMeta(x, y);
            if (meta==0) {
                game.d.rect(x, y, 1, 1, 0xFF9c06ad);
                game.d.translate(x+.5f, y+.5f, 0);
            } else {
                game.d.rect(x, y, 1, 1, 0xFFd40adb);
                game.d.translate(x+.5f, y+.5f, 0);
            }
            if (game.vars.inverted_gravity)
                game.d.scale(1, -1, 1);
            game.d.vertex(vertex, 0xFF0136c6);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int x, int y, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (!collided) {
            game.vars.inverted_gravity^=true;
            game.player.motionY*=-1;
            collided=true;
            game.player.jumps=0;
        }
        return false;
    }

    @Override
    public int metaStates() {
        return 2;
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level, ArrayList<Borrow.BorrowedCollisionData> data) {
        collided=false;
    }

}
