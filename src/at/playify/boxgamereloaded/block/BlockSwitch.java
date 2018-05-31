package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockSwitch extends Block implements NoCollideable {//TODO PACKET
    public static final char chr='m';
    private boolean collided;

    public BlockSwitch(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x, y, x+1, y+1));
    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        if (checkOnly) {
            final float v=.01f;
            return b.collide(bound.set(x-v, y, 1+2*v, 1))||b.collide(bound.set(x, y-v, 1, 1+2*v));
        } else {
            return b.collide(bound.set(x, y, 1, 1));
        }
    }

    @Override
    public void draw(int x, int y, Level level) {
        game.d.pushMatrix();
        game.d.translate(x,y,0);
        if (game.vars.cubic){
            game.d.cube(0,0,0,1,1,1,0xFF7A63FF);
            game.d.cube(.2f,.4f,-.01f,.6f,.2f,.01f,0xFF000000);
            game.d.cube(.1f,.3f,-.1f,.8f,.1f,.1f,0xFFA0A0A0,true,false,true,false);
            game.d.cube(.1f,.3f,-.1f,.1f,.4f,.1f,0xFFA0A0A0,false,true,false,true);
            game.d.cube(.1f,.6f,-.1f,.8f,.1f,.1f,0xFFA0A0A0,true,false,true,false);
            game.d.cube(.8f,.3f,-.1f,.1f,.4f,.1f,0xFFA0A0A0,false,true,false,true);
            game.d.cube(.2f+(.4f*game.vars.switchState),.4f,-.15f,.2f,.2f,.15f,0xFF00FF00);
        }else{
            game.d.rect(.1f,.3f,.8f,.1f,0xFFA0A0A0);
            game.d.rect(.1f,.3f,.1f,.4f,0xFFA0A0A0);
            game.d.rect(.1f,.6f,.8f,.1f,0xFFA0A0A0);
            game.d.rect(.8f,.3f,.1f,.4f,0xFFA0A0A0);
            game.d.rect(.2f+(.4f*game.vars.switchState),.4f,.2f,.2f,0xFF00FF00);
            game.d.rect(.2f,.4f,.6f,.2f,0xFF000000);
            game.d.rect(0,0,1,1,0xFF7A63FF);
        }
        game.d.popMatrix();
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level) {
        collided=false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (!collided) {
            collided=true;
            game.vars.switched^=true;
        }
        return false;
    }
}
