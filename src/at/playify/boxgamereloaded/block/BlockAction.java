package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;

import java.util.ArrayList;

public class BlockAction extends BlockDebug implements NoCollideable{
    public static final char chr='i';
    private boolean collided;

    BlockAction(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public String prefix() {
        return "A";
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (!collided) {
            boolean jump;
            switch (meta) {
                case 0:jump=true;break;
                case 1:jump=player.collidesVert;break;
                case 2:jump=player.collidesHor;break;
                case 3:jump=player.collidesVert&&player.collidesHor;break;
                case 4:jump=Math.abs(player.bound.cy()%1)<.1f;break;
                default:jump=false;
            }
            if (jump) {
                game.player.motionY=0.21f*(((game.player.bound.w()+game.player.bound.h())/2)/0.8f);
                collided=true;
            }
        }
        return false;
    }

    @Override
    public int metaStates() {
        return 5;
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level) {
        collided=false;
    }
}
