package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockIconDrawButton extends BlockDebug {
    public static final char chr='i';

    public BlockIconDrawButton(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public String prefix() {
        return "I";
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        if (!((x|y) >= 0&&x<level.sizeX&&y<level.sizeY)) {
            list.add(Borrow.bound(x, y, x+1, y+1));
        }
    }

    @Override
    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        if ((x|y) >= 0&&x<level.sizeX&&y<level.sizeY) {
            return super.collide(b, x, y, checkOnly, meta, level);
        } else {
            return b.collide(bound.set(x, y));
        }
    }

    @Override
    public int metaStates() {
        return 3;
    }

    @Override
    public void draw(int x, int y, Level level) {
        int meta=level.getMeta(x, y);
        if (game.vars.cubic) {
            game.d.pushMatrix();
            game.d.translate(0,0,-0.001f);
            if (meta==0){
                game.d.rect(x+1/5f,y+2/5f,3/5f,1/5f,0xFF000000);
                game.d.rect(x+2/5f,y+1/5f,1/5f,3/5f,0xFF000000);
            }else if (meta==1) {
                int buttonID=-1;
                if (y==-2)buttonID=0;
                else if (x==-2)buttonID=1;
                else if (y==game.level.sizeY+1)buttonID=2;
                else if (x==game.level.sizeX+1)buttonID=3;
                game.d.pushMatrix();
                game.d.translate(x+.5f,y+.5f,0);
                game.d.rotate(-90*buttonID,0,0,1);
                game.d.vertex(game.vertex.arrow,0xFF000000);
                game.d.popMatrix();
            }else if (meta==2) {
                game.d.rect(x+1/5f,y+2/5f,3/5f,1/5f,0xFF000000);
            }
            game.d.popMatrix();
            game.d.cube(x, y, 0, 1, 1, 1, 0xFFA0A0A0);
            game.d.lineCube(x, y, 0, 1, 1, 1, 0xFF000000);
        } else {
            if (meta==0){
                game.d.rect(x+1/5f,y+2/5f,3/5f,1/5f,0xFF000000);
                game.d.rect(x+2/5f,y+1/5f,1/5f,3/5f,0xFF000000);
            }else if (meta==1) {
                int buttonID=-1;
                if (y==-2)buttonID=0;
                else if (x==-2)buttonID=1;
                else if (y==game.level.sizeY+1)buttonID=2;
                else if (x==game.level.sizeX+1)buttonID=3;
                game.d.pushMatrix();
                game.d.translate(x+.5f,y+.5f,0);
                game.d.rotate(-90*buttonID,0,0,1);
                game.d.vertex(game.vertex.arrow,0xFF000000);
                game.d.popMatrix();
            }else if (meta==2) {
                game.d.rect(x+1/5f,y+2/5f,3/5f,1/5f,0xFF000000);
            }
            game.d.rect(x, y, 1, 1, 0xFFA0A0A0);
            game.d.lineRect(x, y, 1, 1, 0xFF000000);
        }
    }


    @Override
    public boolean isBackGround(int meta) {
        return false;
    }
}
