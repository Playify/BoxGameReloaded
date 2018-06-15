package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public abstract class BlockLiquid extends Block implements NoCollideable {
    private int liquidID;

    public BlockLiquid(BoxGameReloaded game, char c){
        super(game, c);
        liquidID=game.vars.liquid.length;
        boolean[] liquid=new boolean[game.vars.liquid.length+1];
        System.arraycopy(game.vars.liquid,0,liquid,0,game.vars.liquid.length);
        game.vars.liquid=liquid;
    }

    @Override
    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level){
        return false;
    }

    @Override
    public abstract void draw(int x, int y, Level level);

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player){

    }

    @Override
    public boolean isBackGround(int meta){
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data){
        game.vars.liquid[liquidID]=true;
        return false;
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level){
        game.vars.liquid[liquidID]=false;
    }
}
