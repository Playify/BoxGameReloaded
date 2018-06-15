package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockGravityPad extends Block implements Collideable, NoCollideable {
    public static final char chr='y';
    private boolean collided;

    BlockGravityPad(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
    }

    @Override
    public boolean isBackGround(int meta) {
        return true;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return checkOnly&&b.collide(bound.set(x, y, 1, 1));
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.pushMatrix();
            game.d.cube(x, y, 0.9f, 1, 1, .1f, 0xFFd40adb);
            game.d.translate(x+.5f, y+.5f, .899f);
            if (game.vars.gravity)
                game.d.scale(1, -1, 1);
            game.d.vertex(game.vertex.arrow, 0xFF0136c6);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            game.d.rect(x, y, 1, 1, 0xFFd40adb);
            game.d.translate(x+.5f, y+.5f, -0.01f);
            if (game.vars.gravity)
                game.d.scale(1, -1, 1);
            game.d.vertex(game.vertex.arrow, 0xFF0136c6);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (!collided) {
            game.vars.gravity^=true;
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
    public void onNoCollide(PlayerSP player, Level level) {
        collided=false;
    }

}
