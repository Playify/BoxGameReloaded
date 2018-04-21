package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockKill extends Block implements Collideable {
    public static char chr='k';

    BlockKill(BoxGameReloaded game, char c) {
        super(game,c);
    }

    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        return (checkOnly || game.vars.god) && b.collide(bound.set(x,y));
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            if (game.vars.cubic_check) {
                game.d.cube(x, y, 0, 1, 1, 1, 0xFFFF0000, !level.get(x, y + 1).isSolid(), !level.get(x + 1, y).isSolid(), !level.get(x, y - 1).isSolid(), !level.get(x - 1, y).isSolid());
            }else{
                game.d.cube(x, y, 0, 1, 1, 1, 0xFFFF0000);
            }
        }else {
            game.d.rect(x, y, 1, 1,0xFFFF0000);
        }
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x+.1f,y+.1f,x+.8f,y+.8f));
    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int x, int y, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (game.vars.god) {
            return false;
        }else {
            player.killedByBlock();
            return true;
        }
    }
}
