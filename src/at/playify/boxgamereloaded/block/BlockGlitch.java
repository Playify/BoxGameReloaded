package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.BoundingBox;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockGlitch extends Block {
    public static final char chr='d';
    private BoundingBox bound=new BoundingBox(0,0,0,0);

    BlockGlitch(BoxGameReloaded game, char c) {
        super(game,c);
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return b.collide(super.bound.set(x,y));
    }

    @Override
    public void draw(int x, int y, Level level) {
        float x1=Utils.randomFloat();
        float y1=Utils.randomFloat();
        float x2=Utils.randomFloat();
        float y2=Utils.randomFloat();
        this.bound.set(Math.min(x1,x2),Math.min(y1,y2),Math.max(x1,x2),Math.max(y1,y2));
        super.bound.set(x+bound.minX,y+bound.minY,bound.maxX-bound.minX,bound.maxY-bound.minY);
        int clr=Utils.randomInt();
        if (game.vars.cubic) {
            if (game.vars.cubic_check) {
                game.d.cube(super.bound.x(), super.bound.y(), 0, super.bound.w(), super.bound.h(), 1, 0xFF000000|clr, !level.get(x, y + 1).isSolid(), !level.get(x + 1, y).isSolid(), !level.get(x, y - 1).isSolid(), !level.get(x - 1, y).isSolid());
            }else{
                game.d.cube(super.bound.x(), super.bound.y(), 0, super.bound.w(), super.bound.h(), 1, 0xFF000000|clr);
            }
        }else {
            game.d.rect(super.bound.x(), super.bound.y(), super.bound.w(), super.bound.h(),0xFFF00000|clr);
        }
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x,y,x+1,y+1));
    }

    @Override
    public boolean isBackGround(int meta) {
        return false;
    }

    @Override
    public boolean canDraw() {
        return false;
    }
}
