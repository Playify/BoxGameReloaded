package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockNear extends Block implements Collideable, NoCollideable {
    public static final char chr='n';
    private boolean collided;
    private boolean inside;

    BlockNear(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        if (!inside&&(collided||player.collidesVert||player.collidesHor)) {
            list.add(Borrow.bound(x, y, x+1, y+1));
        }
    }

    @Override
    protected boolean isBackGround(int meta) {
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level) {
        if (checkOnly) {
            final float v=.01f;
            return b.collide(bound.set(x-v, y-v, 1+2*v, 1+v+.201f));
        } else if (!inside&&(collided)) {
            return b.collide(bound.set(x, y, 1, 1));
        } else return false;
    }

    @Override
    public void draw(int x, int y, Level level) {
        int color=0xFFEAEAEA;
        boolean b=!inside&&(collided||game.player.collidesVert||game.player.collidesHor);
        if (b) {
            if (game.vars.cubic) {
                game.d.cube(x, y, 0f, 1, 1, 1f, color);
            } else {
                game.d.rect(x, y, 1, 1, color);
            }
        } else {
            float v=.3f;
            if (game.vars.cubic) {
                game.d.cube(x+v, y+v, v, 1-2*v, 1-2*v, 1f-2*v, color);
            } else {
                game.d.rect(x+v, y+v, 1-2*v, 1-2*v, color);
            }
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        if (!collided) {
            if (player.collidesHor||player.collidesVert)
                collided=true;
            for (int i=data.size()-1;i >= 0;i--) {
                Borrow.BorrowedCollisionData dat=data.get(i);
                if (dat.blk==this) {
                    if (player.bound.collide(bound.set(dat.x,dat.y,1,1))) {
                        inside=true;
                        break;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int metaStates() {
        return 1;
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level, ArrayList<Borrow.BorrowedCollisionData> data) {
        collided=false;
        inside=false;
    }
}
