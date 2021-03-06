package at.playify.boxgamereloaded.block;


import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class Block08 extends Block {
    public static final char chr='o';
    Block08(BoxGameReloaded game, char c) {
        super(game,c);
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return b.collide(bound.set(x + .1f, y + .1f, .8f, .8f));
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (level==game.painter.fakeLevel) {
            game.d.translate(.5f, .5f, .5f);
            game.d.scale(.75f);
            game.d.translate(-.5f, -.5f, -.5f);
        }
        if (game.vars.cubic) {
            game.d.cube(x+.1f,y+.1f,.1f,.8f,.8f,.8f, 0xFFFFFF00);
        }else {
            game.d.rect(x+.1f,y+.1f,.8f,.8f,0xFFFFFF00);
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        list.add(Borrow.bound(x+.1f,y+.1f,x+.9f,y+.9f));
    }

    @Override
    public boolean isBackGround(int meta) {
        return false;
    }
}
