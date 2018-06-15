package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;

public class BlockWater extends BlockLiquid {
    public static final char chr='x';
    public BlockWater(BoxGameReloaded game, char c){
        super(game, c);
    }

    @Override
    public void draw(int x, int y, Level level){
        boolean above=level.is(x, y+1, this, 0);
        boolean up=!level.is(x, y+1,this, -3);
        boolean right=!level.is(x+1, y,this, -3);
        boolean down=!level.is(x, y-1,this, -3);
        boolean left=!level.is(x-1, y,this, -3);
        game.d.cube(x,y,0,1, above?1:.85f,1,0xA00000FF, up, right, down, left);

        boolean l=!above&&!left&&level.is(x-1, y+1, this, 0);
        boolean r=!above&&!right&&level.is(x+1, y+1, this, 0);
        if (l||r){
            game.d.cube(x+1,y+.85f,0,-1, .15f,1,0xA00000FF, false,l,false,r,false,false);
        }
    }

    @Override
    public boolean isSolid(){
        return false;
    }
}
