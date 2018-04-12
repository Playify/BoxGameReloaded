package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

//Leiter
public class BlockLadder extends Block implements Collideable{
    public BlockLadder(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x,y,x+1,y+1));
    }

    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        final float v=.01f;
        return b.collide(bound.set(x-v,y-v,1+2*v,1+2*v));
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            if (game.vars.cubic_check) {
                game.d.cube(x, y, 0, 1, 1, 1, 0xFFA8A346, !level.get(x, y + 1).isSolid(), !level.get(x + 1, y).isSolid(), !level.get(x, y - 1).isSolid(), !level.get(x - 1, y).isSolid());
            }else{
                game.d.cube(x, y, 0, 1, 1, 1, 0xFFA8A346);
            }
        }else {
            game.d.rect(x, y, 1, 1,0xFFA8A346);
        }
    }

    @Override
    public boolean onCollide(PlayerSP player) {
        if (player.collidesHor) {
            player.motionY=Math.max(.1f,player.motionY);
        }else{
            player.motionY=Math.max(-.05f,player.motionY);
        }
        player.jumps=0;
        return false;
    }
}
