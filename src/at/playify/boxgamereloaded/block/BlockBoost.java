package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockBoost extends Block implements Collideable{
    public static final char chr='j';
    BlockBoost(BoxGameReloaded game, char c) {
        super(game,c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return b.collide(bound.set(x,y));
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        if (Math.abs(player.motionY)<0.015f) {
            Borrow.BorrowedBoundingBox b = Borrow.bound(x, y, x + 1, y + 1);
            b.left=b.right=false;
            b.down=!(b.up=game.vars.gravity);
            list.add(b);
        }
    }

    @Override
    public boolean isBackGround(int meta) {
        return true;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (Math.abs(player.motionY) <= 0.02f)
            player.motionY =(player.jumpKey ? 0.2f : 0.02f);
        else
            player.motionY =Math.abs(player.motionY) * (player.jumpKey ? 1.1f : 0.95f);
        player.jumps = game.vars.jumps;
        return false;
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.cube(x, y, 0.9f, 1, 1, .1f,0xFF00C656);
            game.d.pushMatrix();
            game.d.translate(x+.5f,y+.5f,.9f);
            game.d.rotate(game.vars.gravity ?-15:15,-1,0,0);
            game.d.rotate(45,0,0,1);
            final float v=.3f;
            game.d.cube(-v,-v,0,2*v,2*v,.2f, 0xFF59FF59);
            game.d.popMatrix();
        }else {
            game.d.rect(x, y, 1, 1, 0xFF00C656);
            game.d.pushMatrix();
            game.d.translate(x+.5f,y+.5f,-0.01f);
            game.d.scale(1,game.vars.gravity ?1:-1,1);
            game.d.vertex(game.vertex.bigtriangle,0xFF59FF59);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
