package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

//Leiter
public class BlockLadder extends Block implements Collideable{
    public static final char chr='l';

    BlockLadder(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x,y,x+1,y+1));
    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        final float v=.01f;
        return b.collide(bound.set(x-v,y-v,1+2*v,1+2*v));
    }

    @Override
    public void draw(int x, int y, Level level) {
        game.d.pushMatrix();
        game.d.translate(x+.5f,y,0);
        game.d.scale(1/8f);

        Block l=level.get(x-1, y,game.blocks.GROUND);
        Block r=level.get(x+1, y,game.blocks.GROUND);
        boolean left=l instanceof BlockGround;
        boolean right=r instanceof BlockGround;
        left|=l instanceof BlockLadder;
        right|=r instanceof BlockLadder;
        if (left&&right)left=right=false;
        game.d.cube(left?-4:-3,0,0,left||right?7:6,8,8,0xFFFFFF00);
        if (!left&&!right)left=right=true;
        drawSide(left);
        game.d.scale(-1,1,1);
        drawSide(right);
        game.d.popMatrix();
    }

    private void drawSide(boolean b) {
        if (b) {
            game.d.cube(3,1,.5f,1,2,7,0xFFA8A346,true,true,true,false);
            game.d.cube(3,5,.5f,1,2,7,0xFFA8A346,true,true,true,false);
        }
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (player.collidesHor) {
            player.motionY=Math.max(.1f,player.motionY);
        }else{
            player.motionY=Math.max(-.05f,player.motionY);
        }
        player.jumps=0;
        return false;
    }

    @Override
    public String name(int data) {
        return "Ladder";
    }
}
