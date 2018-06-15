package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

//Ground
public class BlockGround extends Block {
    public static final char chr='g';

    BlockGround(BoxGameReloaded game, char c) {
        super(game,c);
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return b.collide(bound.set(x,y));
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            if (game.vars.cubic_check) {
                game.d.cube(x, y, 0, 1, 1, 1, 0xFFFFFF00, !level.get(x, y + 1).isSolid(), !level.get(x + 1, y).isSolid(), !level.get(x, y - 1).isSolid(), !level.get(x - 1, y).isSolid());
            }else{
                game.d.cube(x, y, 0, 1, 1, 1, 0xFFFFFF00);
            }
        }else {
            game.d.rect(x, y, 1, 1,0xFFFFFF00);
        }
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x,y,x+1,y+1));
    }

    @Override
    public boolean isBackGround(int meta) {
        return false;
    }
}
