package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.FakeLevel;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockSwitched extends Block {
    public static final char chr='p';

    public BlockSwitched(BoxGameReloaded game, char c){
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player){
        int meta=level.getMeta(x, y);
        if (is(meta)) {
            Borrow.BorrowedBoundingBox b=Borrow.bound(x, Utils.clamp(player.bound.yh(), y, y+1), x+1, Utils.clamp(player.bound.y(), y, y+1));
            b.left=b.right=false;
            list.add(b);
            b=Borrow.bound(Utils.clamp(player.bound.xw(), x, x+1), y, Utils.clamp(player.bound.x(), x, x+1), y+1);
            b.up=b.down=false;
            list.add(b);
        }
    }

    private boolean is(int meta){
        switch (meta) {
            case 0: return game.connection.switch0;
            case 1: return !game.connection.switch0;
            case 2: return !game.connection.switch1;
            case 3: return game.connection.switch1;
        }
        return true;
    }

    @Override
    public boolean isBackGround(int meta){
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level){
        if (checkOnly) {
            final float v=.01f;
            return b.collide(bound.set(x-v, y, 1+2*v, 1))||b.collide(bound.set(x, y-v, 1, 1+2*v));
        } else if (is(meta)) {
            return b.collide(bound.set(x, y, 1, 1));
        } else {
            return false;
        }
    }

    @Override
    public void draw(int x, int y, Level level){
        game.d.pushMatrix();
        game.d.translate(x, y, 0);
        float state=1;
        int color=0xFF000000;
        switch (level.getMeta(x, y)) {
            case 0: color=0xFFFF0000;
                state=game.vars.switchState0;
                break;
            case 1: color=0xFF00FF00;
                state=1-game.vars.switchState0;
                break;
            case 2: color=0xFF8000FF;
                state=1-game.vars.switchState1;
                break;
            case 3: color=0xFF8000FF;
                state=game.vars.switchState1;
                break;
        }
        if (level instanceof FakeLevel) state=1;
        float state2=state*.9f+.1f;
        if (game.vars.cubic) {
            game.d.translate(0, 0, 1-state2);
            float v=.3f;
            game.d.cube(0, 0, 0, v, v, state2, 0xFF7A63FF);
            game.d.cube(1-v, 0, 0, v, v, state2, 0xFF7A63FF);
            game.d.cube(0, 1-v, 0, v, v, state2, 0xFF7A63FF);
            game.d.cube(1-v, 1-v, 0, v, v, state2, 0xFF7A63FF);
            game.d.pushMatrix();
            game.d.translate(.5f,.5f,0);
            float vv=.1f-.1f*state;
            for (int i=0;i<4;i++) {
                game.d.cube(vv, vv, .25f*state2, .399f, .399f, .749f*state2, color);
                int sc=(i&1)*2-1;
                game.d.scale(sc,-sc);
                game.d.flipCullface();
            }
            game.d.popMatrix();
        } else {
            float v=.3f;
            game.d.rect(0, 0,  v, v,  0xFF7A63FF);
            game.d.rect(1-v, 0,  v, v,  0xFF7A63FF);
            game.d.rect(0, 1-v,  v, v,  0xFF7A63FF);
            game.d.rect(1-v, 1-v,  v, v,  0xFF7A63FF);
            game.d.pushMatrix();
            game.d.translate(.5f,.5f,0);
            float vv=.1f-.1f*state;
            for (int i=0;i<4;i++) {
                game.d.rect(vv, vv,  .399f, .399f,  color);
                int sc=(i&1)*2-1;
                game.d.scale(sc,-sc);
                game.d.flipCullface();
            }
            game.d.popMatrix();
        }
        game.d.popMatrix();
    }

    @Override
    public int metaStates(){
        return 3;
    }
}
