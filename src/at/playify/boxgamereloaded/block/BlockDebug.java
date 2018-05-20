package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public abstract class BlockDebug extends Block {

    public BlockDebug(BoxGameReloaded game, char c) {
        super(game, c);
    }

    public abstract String prefix();

    @Override
    public boolean canDraw() {
        return game.vars.debug.console;
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
            game.d.drawStringCenter(prefix()+level.getMeta(x,y),.5f,0.25f,.5f, 0x66000000);
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            game.d.rect(x, y, 1, 1, 0xFFd40adb);
            game.d.translate(x,y,0);
            game.d.drawStringCenter(prefix()+level.getMeta(x,y),.5f,0.25f,.5f, 0x66000000);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
