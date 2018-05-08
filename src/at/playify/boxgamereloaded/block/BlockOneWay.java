package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockOneWay extends Block {
    public static final char chr='w';

    BlockOneWay(BoxGameReloaded game, char c) {
        super(game,c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        return b.collide(bound.set(x,y));
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        int meta=level.getMeta(x,y);
        Borrow.BorrowedBoundingBox blk=Borrow.bound(x, y, x+1, y+1);
        if (blk.intersects(bound)) {
            if (meta==0) {
                Borrow.BorrowedBoundingBox box=Borrow.bound(player.bound.x(), y, player.bound.x(), y+1);
                if (box.minX<=x+1) {
                    list.add(box);
                }else{
                    box.free();
                }
            } else if (meta==1) {
                Borrow.BorrowedBoundingBox box=Borrow.bound(player.bound.xw(), y, player.bound.xw(), y+1);
                if (box.minX>=x) {
                    list.add(box);
                }else{
                    box.free();
                }
            } else if (meta==2) {
                Borrow.BorrowedBoundingBox box=Borrow.bound(x,player.bound.yh(), x+1, player.bound.yh());
                if (box.minY>=y) {
                    list.add(box);
                }else{
                    box.free();
                }
            }else if (meta==3) {
                Borrow.BorrowedBoundingBox box=Borrow.bound(x,player.bound.y(), x+1,player.bound.y());
                if (box.minY<=y+1) {
                    list.add(box);
                }else{
                    box.free();
                }
            }
        }
        blk.right=(meta!=0&&level.get(x-1,y)!=this)||meta==1;
        blk.left=(meta!=1&&level.get(x+1,y)!=this)||meta==0;
        blk.down=(meta!=2&&level.get(x,y+1)!=this)||meta==3;
        blk.up=(meta!=3&&level.get(x,y-1)!=this)||meta==2;
        list.add(blk);



        /*
        if (meta==0){
            if (bound.x()>player.bound.cx()+0.0001f) {
                list.add(Borrow.bound(x,y,x+1,y+1));
            }
            if (!(level.get(x,y-1) instanceof BlockOneWay&&level.getMeta(x,y-1)==0))
            list.add(Borrow.bound(x,y,x+1,y));
            if (!(level.get(x,y+1) instanceof BlockOneWay&&level.getMeta(x,y+1)==0))
            list.add(Borrow.bound(x,y+1,x+1,y+1));
        }else if (meta==2){
            if (bound.x()+0.0001f<player.bound.cx()) {
                list.add(Borrow.bound(x,y,x+1,y+1));
            }
            if (!(level.get(x,y-1) instanceof BlockOneWay&&level.getMeta(x,y-1)==2))
                list.add(Borrow.bound(x,y,x+1,y));
            if (!(level.get(x,y+1) instanceof BlockOneWay&&level.getMeta(x,y+1)==2))
                list.add(Borrow.bound(x,y+1,x+1,y+1));
        }else if (meta==1){

        }else if (meta==3){

        }*/
    }

    @Override
    protected boolean isBackGround(int meta) {
        return true;
    }

    @Override
    public void draw(int x, int y, Level level) {
        game.d.pushMatrix();
        int meta=level.getMeta(x,y);
        if (game.vars.cubic) {
            game.d.cube(x, y, 0.9f, 1, 1, .1f, 0xFF009DFF);
            game.d.translate(x+.5f,y+.5f,.9f);
            game.d.rotate(15f,meta==2?1:meta==3?-1:0,meta==0?1:meta==1?-1:0,0);
            game.d.rotate(45,0,0,1);
            final float v=.3f;
            game.d.cube(-v,-v,0,2*v,2*v,.2f,0xFF00FFFF);
        }else {
            game.d.rect(x, y, 1, 1,0xFF009DFF);
            game.d.translate(x+.5f,y+.5f,-0.01f);
            game.d.rotate(((meta&2)==0?90:0)+((meta&1)==0?0:180),0,0,1);
            game.d.vertex(game.vertex.bigtriangle,0xFF00FFFF);
        }
        game.d.popMatrix();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public int metaStates() {
        return 4;
    }
}
