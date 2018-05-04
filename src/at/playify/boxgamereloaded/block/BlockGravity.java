package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockGravity extends Block implements Collideable, NoCollideable {
    public static final char chr='v';
    private boolean collided;

    BlockGravity(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x, y, x+1, y+1));
    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        if (checkOnly) {
            final float v=.01f;
            return b.collide(bound.set(x-v, y, 1+2*v, 1))||b.collide(bound.set(x, y-v, 1, 1+2*v));
        } else {
            return b.collide(bound.set(x, y, 1, 1));
        }
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.pushMatrix();
                game.d.cube(x, y, 0f, 1, 1, 1f, 0xFF9c06ad);
                game.d.translate(x+.5f, y+.5f, -.001f);
            if (game.vars.inverted_gravity)
                game.d.scale(1, -1, 1);
            game.d.vertex(game.vertex.arrow, 0xFF0136c6);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
                game.d.rect(x, y, 1, 1, 0xFF9c06ad);
                game.d.translate(x+.5f, y+.5f, -0.01f);
            if (game.vars.inverted_gravity)
                game.d.scale(1, -1, 1);
            game.d.vertex(game.vertex.arrow, 0xFF0136c6);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
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
        return 1;
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level, ArrayList<Borrow.BorrowedCollisionData> data) {
        collided=false;
    }
}
