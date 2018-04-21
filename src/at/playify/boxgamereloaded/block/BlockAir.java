package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

//Luft
public class BlockAir extends Block {
    public static final char chr='a';
    BlockAir(BoxGameReloaded game, char c) {
        super(game,c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return false;
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (level==game.painter.fakeLevel) {
            if (game.vars.cubic) {
                if (game.vars.cubic_check) {
                    game.d.cube(x, y, 0, 1, 1, 1, 0xFF30ABD9, !level.get(x, y+1).isSolid(), !level.get(x+1, y).isSolid(), !level.get(x, y-1).isSolid(), !level.get(x-1, y).isSolid());
                } else {
                    game.d.cube(x, y, 0, 1, 1, 1, 0xFF30ABD9);
                }
            } else {
                game.d.rect(x, y, 1, 1, 0xFF30ABD9);
            }
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {

    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }
}
