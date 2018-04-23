package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockArrow extends Block {
    public static final char chr='r';

    BlockArrow(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return false;
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
    }

    @Override
    protected boolean isBackGround(int meta) {
        return true;
    }

    @Override
    public void draw(int x, int y, Level level) {
        boolean r, up;
        if (r=(level.get(x+1, y) instanceof BlockArrow)) {
            game.d.drawLine(x+.5f, y+.5f, 0, 1, 0, 0, 0xFF000000);
        }
        if (up=(level.get(x, y+1) instanceof BlockArrow)) {
            game.d.drawLine(x+.5f, y+.5f, 0, 0, 1, 0, 0xFF000000);
        }
        boolean point=true;
        if (!r) {
            if (!up) {
                if (level.get(x+1, y+1) instanceof BlockArrow) {
                    game.d.drawLine(x+.5f, y+.5f, 0, 1, 1, 0, 0xFF000000);
                    point=false;
                }
            } else point=false;
            boolean d=(level.get(x, y-1) instanceof BlockArrow);
            if (!d) {
                if (level.get(x+1, y-1) instanceof BlockArrow) {
                    game.d.drawLine(x+.5f, y+.5f, 0, 1, -1, 0, 0xFF000000);
                    point=false;
                }
            } else point=false;
        } else point=false;
        int meta=level.getMeta(x, y);
        if (meta==9) {
            point=true;
        } else {
            if (point&&level.get(x-1, y)==this) point=false;
            if (point&&level.get(x-1, y-1)==this) point=false;
            if (point&&level.get(x-1, y+1)==this) point=false;
        }
        if (meta>0&&meta<9) {
            meta++;
            game.d.pushMatrix();
            game.d.translate(x+.5f, y+.5f, 0);
            game.d.rotate(45*meta, 0, 0, 1);
            game.d.translate(0, -.1f, 0);
            game.d.vertex(game.vertex.arrow, 0xFF000000);
            point=false;
            game.d.popMatrix();
        }
        if (point) {
            game.d.point(x+.5f, y+.5f, 0, 0xFF000000, 30);
        }
    }

    @Override
    public void draw(int meta) {
        game.d.translate(.5f, .5f, .5f);
        game.d.scale(2, 2, 1);
        game.d.rotate(90, 0, 0, 1);
        game.d.vertex(game.vertex.arrow, 0xFF000000);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public int metaStates() {
        return 10;
    }
}
