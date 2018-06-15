package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockBorder extends Block {
    public static char chr='b';

    BlockBorder(BoxGameReloaded game, char c){
        super(game, c);
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level){
        return b.collide(bound.set(x, y));
    }

    @Override
    public void draw(int x, int y, Level level){
        final float v=.2f;
        //Counter Clock Wise

        if (game.vars.cubic) {
            boolean up=connect(x, y-1, level), left=connect(x-1, y, level), down=connect(x, y+1, level), right=connect(x+1, y, level);
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
            if (up=connect(x, y-1, level)) game.d.rect(x, y, 1, v, 0xFF00FFFF);
            if (left=connect(x-1, y, level)) game.d.rect(x, y, v, 1, 0xFF00FFFF);
            if (down=connect(x, y+1, level)) game.d.rect(x, y+1-v, 1, v, 0xFF00FFFF);
            if (right=connect(x+1, y, level)) game.d.rect(x+1-v, y, v, 1, 0xFF00FFFF);
            //CORNERS
            if (!up&&!left&&connect(x-1, y-1, level)) game.d.rect(x, y, v, v, 0xFF00FFFF);
            if (!down&&!left&&connect(x-1, y+1, level)) game.d.rect(x, y+1-v, v, v, 0xFF00FFFF);
            if (!down&&!right&&connect(x+1, y+1, level)) game.d.rect(x+1-v, y+1-v, v, v, 0xFF00FFFF);
            if (!up&&!right&&connect(x+1, y-1, level)) game.d.rect(x+1-v, y, v, v, 0xFF00FFFF);
        }
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player){
        list.add(Borrow.bound(x, y, x+1, y+1));
    }

    @Override
    public boolean isBackGround(int meta){
        return false;
    }

    private boolean connect(int x, int y, Level level){
        return !(level.get(x, y) instanceof BlockBorder);
    }
}
