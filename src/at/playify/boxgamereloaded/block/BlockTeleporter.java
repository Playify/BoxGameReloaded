package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class BlockTeleporter extends Block implements Collideable, NoCollideable {
    public static final char chr='t';
    private boolean collided;
    private RectBound bnd=new RectBound();
    private float[] vertex3d=new float[]{
            -1, 1, 0,
            0, 0, -0.25f,
            0, .7f, 0,
            1, 1, 0,
            0, 0, -0.25f,
            0, .7f, 0
    };
    private float[] vertex=new float[]{
            -1, 1, 0,
            0, 0, 0,
            0, .7f, 0,
            1, 1, 0,
            0, 0, 0,
            0, .7f, 0
    };

    BlockTeleporter(BoxGameReloaded game, char c) {
        super(game, c);
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
        if (game.vars.cubic) {
            game.d.pushMatrix();
            game.d.cube(x, y, 0.9f, 1, 1, .1f, 0xFFA991FF);
            game.d.translate(x+.5f, y+.5f, .899f);
            game.d.scale(1/3f);
            for (int i=0;i<4;i++) {
                game.d.vertex(vertex3d, Utils.hsvToRgb(level.getMeta(x, y)/(float) metaStates()));
                game.d.rotate(90, 0, 0, 1);
            }
            game.d.popMatrix();
        } else {
            game.d.pushMatrix();
            game.d.rect(x, y, 1, 1, 0xFFA991FF);
            game.d.translate(x+.5f, y+.5f, 0);
            game.d.scale(1/3f);
            for (int i=0;i<4;i++) {
                game.d.vertex(vertex, Utils.hsvToRgb(level.getMeta(x, y)/(float) metaStates()));
                game.d.rotate(90, 0, 0, 1);
            }
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (!collided) {
            Borrow.BorrowedCollisionData col=null;
            for (int i=0;i<data.size();i++) {
                Borrow.BorrowedCollisionData borrowedCollisionData=data.get(i);
                if (borrowedCollisionData.blk==this&&borrowedCollisionData.meta==meta) {
                    col=borrowedCollisionData;
                }
            }
            if (col!=null) {
                Borrow.BorrowedCollisionData d=level.findNext(col);
                float dx=d.x-col.x;
                float dy=d.y-col.y;
                if (dx==0&&dy==0) {
                    collided=true;
                } else {
                    bnd.set(player.bound).move(dx, dy);
                    if (!level.collide(bnd)) {
                        game.zoom_x+=dx;
                        game.zoom_y+=dy;
                        player.bound.set(bnd);
                        collided=true;
                    }
                }
                d.free();
            }
        }
        return true;
    }

    @Override
    public int metaStates() {
        return 8;
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level, ArrayList<Borrow.BorrowedCollisionData> data) {
        collided=false;
    }
}
