package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockCheckPoint extends Block implements Collideable{
    public static char chr='c';

    BlockCheckPoint(BoxGameReloaded game, char c) {
        super(game,c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        return b.collide(bound.set(x,y));
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {

    }

    @Override
    protected boolean isBackGround(int meta) {
        return true;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int x, int y, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        game.vars.check.check();
        return false;
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.cube(x, y, 0.9f, 1, 1, .1f,0xFFA86246);
            game.d.pushMatrix();
            game.d.translate(x+.5f,y+.5f,.9f);
            game.d.cube(-.3f,-.3f,-0.1f,.6f,.1f,.2f, 0xFFA58B81,true,false,true,false);
            game.d.cube(-.3f,.2f,-0.1f,.6f,.1f,.2f, 0xFFA58B81,true,false,true,false);
            game.d.cube(-.3f,-.3f,-0.1f,.1f,.6f,.2f, 0xFFA58B81,false,true,false,true);
            game.d.cube(.2f,-.3f,-0.1f,.1f,.6f,.2f, 0xFFA58B81,false,true,false,true);
            game.d.popMatrix();
        }else {
            game.d.rect(x, y, 1, 1,0xFFA86246);
            game.d.pushMatrix();
            game.d.translate(x+.5f,y+.5f,-.01f);
            game.d.rect(-.3f,-.3f,.6f,.1f, 0xFFA58B81);
            game.d.rect(-.3f,.2f,.6f,.1f, 0xFFA58B81);
            game.d.rect(-.3f,-.3f,.1f,.6f, 0xFFA58B81);
            game.d.rect(.2f,-.3f,.1f,.6f, 0xFFA58B81);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
