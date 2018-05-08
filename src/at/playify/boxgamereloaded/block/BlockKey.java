package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockKey extends Block implements Collideable {
    public static final char chr='q';

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
        game.d.pushMatrix();
        game.d.translate(x,y,0);
        game.vertex.drawKey(true,level.getMeta(x,y));
        game.d.popMatrix();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        for (int i=0;i<data.size();i++) {
            Borrow.BorrowedCollisionData d=data.get(i);
            if (d.blk==this) {
                game.vars.keys[d.meta]=true;
            }
        }
        return true;
    }

    @Override
    public int metaStates() {
        return 8;
    }

    @Override
    public String name(int data) {
        return "Key [WIP]";
    }
}