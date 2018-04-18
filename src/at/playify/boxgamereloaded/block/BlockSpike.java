package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class BlockSpike extends Block implements Collideable {
    public static char chr='s';
    private float[] vertex=new float[9];
    private float[] vertex1=new float[9];
    private float[] vertex2=new float[9];
    private float[] vertex3=new float[9];

    {
        float x=-.5f, y=-.5f;
        int meta=0;
        vertex[0]=x+1;
        vertex[1]=y+1;
        vertex[2]=0;
        vertex[3]=x;
        vertex[4]=y+1;
        vertex[5]=0;
        vertex[6]=x+.5f;
        vertex[7]=y;
        vertex[8]=0.5f;
        System.arraycopy(vertex, 0, vertex1, 0, vertex.length);
        vertex[0]=x;
        vertex[1]=y+1;
        vertex[2]=0;
        vertex[3]=x;
        vertex[4]=y+1;
        vertex[5]=1;
        System.arraycopy(vertex, 0, vertex2, 0, vertex.length);
        vertex[0]=x+1;
        vertex[1]=y+1;
        vertex[2]=0;
        vertex[3]=x+1;
        vertex[4]=y+1;
        vertex[5]=1;
        System.arraycopy(vertex, 0, vertex3, 0, vertex.length);
        vertex[0]=x+1;
        vertex[1]=y+1;
        vertex[2]=0;
        vertex[3]=x;
        vertex[4]=y+1;
        vertex[5]=0;
        vertex[6]=x+.5f;
        vertex[7]=y;
        vertex[8]=0;
    }

    BlockSpike(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        return (checkOnly||game.vars.god)&&b.collide(bound.set(x, y, 1, 1))&&collide(bound.sizeOf(b), x, y, meta);
    }

    private boolean collide(RectBound b, int x, int y, int meta) {
        if (meta==2) {
            //up
            return (b.x()-x)*2<1+(b.yh()-y)&&(b.xw()-x)*2>1-(b.yh()-y);
        } else if (meta==3) {
            //left
            return (b.y()-y)*2<1+(b.xw()-x)&&(b.yh()-y)*2>1-(b.xw()-x);
        } else if (meta==0) {
            //down
            return (b.xw()-x)*2>(b.y()-y)&&-(b.x()-x)*2>(b.y()-y-2);
        } else if (meta==1) {
            //right
            return (b.yh()-y)*2>(b.x()-x)&&-(b.y()-y)*2>(b.x()-x-2);
        }
        return false;
    }

    @Override
    public void draw(int x, int y, Level level) {
        int meta=level.getMeta(x, y);
        Drawer d=game.d;
        d.pushMatrix();
        d.translate(x+.5f, y+.5f);
        d.rotate(-90*(meta-2), 0, 0, 1);
        if (game.vars.cubic) {
            d.vertex(vertex1, 0xFFFF0000, 0.95f);
            d.vertex(vertex2, 0xFFFF0000, (meta&1)!=0 ? 0.8f : 0.9f);
            d.vertex(vertex3, 0xFFFF0000, (meta&1)!=0 ? 0.8f : 0.9f);
            if (d.back()) {
                d.translate(0, 0, 1);
                d.scale(1, 1, -1);
                d.vertex(vertex1, 0xFFFF0000, 0.95f);
            }
            d.cube(-.5f, -.5f, 0, 1, 1, 1, 0xFFFF0000, true, false, false, false, false, false);
        } else {
            d.vertex(vertex, 0xFFFF0000);
        }
        d.popMatrix();
    }

    @Override
    public int metaStates() {
        return 4;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {

    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int x, int y, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        player.killedByBlock();
        return true;
    }

}
