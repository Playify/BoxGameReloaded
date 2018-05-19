package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.FakeLevel;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class BlockKeyhole extends Block {
    public static final char chr='h';
    public float[] state=new float[8];

    BlockKeyhole(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        if (!game.vars.keys[level.getMeta(x, y)]) {
            list.add(Borrow.bound(x, y, x+1, y+1));
        }
    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return (checkOnly||!game.vars.keys[meta])&&b.collide(bound.set(x, y, 1, 1));
    }

    @Override
    public void draw(int x, int y, Level level) {
        game.d.pushMatrix();
        game.d.translate(x, y, 0);
        int meta=level.getMeta(x, y);
        RectBound bound=game.player.bound;
        float v;
        if (game.vars.keys[meta]&&!(level instanceof FakeLevel)) {
            float xx=bound.cx()-x-.5f;
            float yy=bound.cy()-y-.5f;
            double dist=Math.sqrt(xx*xx+yy*yy);
            float a=1.5f, b=3f;
            v=dist<a ? 1 : (dist>a+b ? 0 : (float) (Math.cos(Math.PI*(dist-a)/b)+1)/2);
        }else v=0;
        v*=.9f;
        game.d.cube(0, 0, v, 1, 1, 1-v, 0xFFFF4F49);
        game.d.translate(0,0,v-3/5f);
        game.vertex.drawKey(false,meta);
        game.d.popMatrix();
    }

    @Override
    public boolean isSolid() {
        return false;
    }
    @Override
    public int metaStates() {
        return 8;
    }

    @Override
    public String name(int data) {
        return "Keyhole";
    }
}