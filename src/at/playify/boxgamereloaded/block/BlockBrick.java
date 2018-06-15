package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockBrick extends Block {
    public static char chr='b';

    BlockBrick(BoxGameReloaded game, char c){
        super(game, c);
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level){
        return b.collide(bound.set(x, y));
    }

    @Override
    public void draw(int x, int y, Level level){
        game.d.pushMatrix();
        int meta=level.getMeta(x, y);
        if (meta==0) {
            game.d.translate(x, y, 0);
            if (game.vars.cubic) {
                game.d.cube(.1f, 0, .1f, level.is(x+1, y, this, 0) ? 1 : .8f, 1, .8f, 0xFFE4DBCD);
                game.d.scale(1/8f, 1/8f, 1/8f);
                game.d.translate(4, 4, 4);
                int color=0xFFC57447;
                for (int i=0;i<2;i++) {
                    game.d.cube(-4, -3, -4, 4, 2, 2, color, true, true, true, true, true, true);
                    game.d.cube(2, -3, -4, 2, 2, 4, color, true, true, true, true, true, true);
                    game.d.cube(-4, 1, -4, 2, 2, 4, color, true, true, true, true, true, true);
                    game.d.cube(0, 1, -4, 4, 2, 2, color, true, true, true, true, true, true);
                    game.d.scale(-1, 1, -1);
                }
            } else {
                game.d.pushMatrix();
                game.d.scale(1/8f, 1/8f, 1);
                game.d.translate(4, 4, 0);
                int color=0xFFC57447;
                game.d.rect(-4, -3, 4, 2, color);
                game.d.rect(2, -3, 2, 2, color);
                game.d.rect(-4, 1, 2, 2, color);
                game.d.rect(0, 1, 4, 2, color);
                game.d.popMatrix();
                boolean b=level.is(x-1, y, this, 0);
                game.d.rect(b?-.1f:.1f, 0, b ? 1 : .8f, 1, 0xFFE4DBCD);
            }
        } else if (meta==1) {

            final float v=.2f;
            //Counter Clock Wise

            if (game.vars.cubic) {
                boolean up=!level.is(x, y-1, this, 1), left=!level.is(x-1, y, this, 1), down=!level.is(x, y+1, this, 1), right=!level.is(x+1, y, this, 1);
                float u=up ? 0 : v;
                float d=down ? 0 : v;
                float l=left ? 0 : v;
                float r=right ? 0 : v;

                //EDGES
                if (up) game.d.cube(x-l, y, 0, 1+l+r, v, 1, 0xFF00FFFF, true, false, true, false);
                if (left) game.d.cube(x, y-u, 0, v, 1+d+u, 1, 0xFF00FFFF, false, true, false, true);
                if (down) game.d.cube(x-l, y+1-v, 0, 1+l+r, v, 1, 0xFF00FFFF, true, false, true, false);
                if (right) game.d.cube(x+1-v, y-u, 0, v, 1+d+u, 1, 0xFF00FFFF, false, true, false, true);
            } else {
                boolean up, left, down, right;
                //EDGES
                if (up=!level.is(x, y-1, this, 1)) game.d.rect(x, y, 1, v, 0xFF00FFFF);
                if (left=!level.is(x-1, y, this, 1)) game.d.rect(x, y, v, 1, 0xFF00FFFF);
                if (down=!level.is(x, y+1, this, 1)) game.d.rect(x, y+1-v, 1, v, 0xFF00FFFF);
                if (right=!level.is(x+1, y, this, 1)) game.d.rect(x+1-v, y, v, 1, 0xFF00FFFF);
                //CORNERS
                if (!up&&!left&&!level.is(x-1, y-1, this, 1)) game.d.rect(x, y, v, v, 0xFF00FFFF);
                if (!down&&!left&&!level.is(x-1, y+1, this, 1)) game.d.rect(x, y+1-v, v, v, 0xFF00FFFF);
                if (!down&&!right&&!level.is(x+1, y+1, this, 1)) game.d.rect(x+1-v, y+1-v, v, v, 0xFF00FFFF);
                if (!up&&!right&&!level.is(x+1, y-1, this, 1)) game.d.rect(x+1-v, y, v, v, 0xFF00FFFF);
            }
        }
        game.d.popMatrix();
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player){
        list.add(Borrow.bound(x, y, x+1, y+1));
    }

    @Override
    public boolean isBackGround(int meta){
        return false;
    }

    @Override
    public int metaStates(){
        return 2;
    }
}
