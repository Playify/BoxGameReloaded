package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockAction extends Block implements Collideable,NoCollideable{
    public static final char chr='i';
    private boolean collided;

    BlockAction(BoxGameReloaded game, char c) {
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
        if (!game.painter.draw)return;
        if (game.vars.cubic) {
            game.d.pushMatrix();
            game.d.translate(x,y, .899f);
            game.d.cube(0,0, 0.01f, 1, 1, .1f, 0xFFd40adb);
            game.d.drawStringCenter(level.getMeta(x,y)+"",.5f,0.25f,.5f);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            game.d.rect(x, y, 1, 1, 0xFFd40adb);
            game.d.translate(x,y,0);
            game.d.drawStringCenter(level.getMeta(x,y)+"",.5f,0.25f,.5f);
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
