package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class BlockResize extends Block implements Collideable {
    public static char chr = 'z';
    private final RectBound bnd = new RectBound(0, 0, 1, 1);

    BlockResize(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        return checkOnly && b.collide(bound.set(x, y));
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {
        if (Math.abs(player.motionY) < 0.015f) {
            Borrow.BorrowedBoundingBox b = Borrow.bound(x, y, x + 1, y + 1);
            b.left = b.right = false;
            b.down = !(b.up = game.vars.inverted_gravity);
            list.add(b);
        }
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int x, int y, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        int count = 0;
        float size = 0;
        for (Borrow.BorrowedCollisionData c : data) {
            if (c.blk == this) {
                switch (c.meta) {
                    case 2:
                        size += .4;
                        break;
                    case 0:
                        size += .8;
                        break;
                    case 1:
                        size += 1.2;
                        break;
                }
                count++;
            }
        }
        size /= count;

        float w = player.bound.w(), h = player.bound.h();
        float ww = (w - size) / 10f;
        float hh = (h - size) / 10f;
        if (Math.abs(w - size) < 0.02f) ww = w - size;
        if (Math.abs(h - size) < 0.02f) hh = h - size;
        for (int i = 0; i < 10; i++) {
            bnd.set(player.bound);
            float cx = bnd.cx();
            bnd.w(w - ww);
            bnd.cx(cx);
            bnd.h(h - hh);
            if (!level.collide(bnd, player)) {
                player.bound.set(bnd);
                break;
            } else {
                hh /= 2;
                ww /= 2;
            }
        }

        return false;
    }

    private void move(PlayerSP player, float moveX, float moveY) {
        float wantX = moveX;
        float wantY = moveY;
        Borrow.BorrowedBoundingBox bound = Borrow.bound(player.bound.x(), player.bound.y(), player.bound.xw(), player.bound.yh());
        //Bound erweitern
        Borrow.BorrowedBoundingBox boundingBox = bound.addCoord(moveX, moveY);
        ArrayList<Borrow.BorrowedBoundingBox> list1 = game.level.getCollisionBoxes(player, boundingBox);
        boundingBox.free();

        //Eigentliche Bewegung anhand der Kollisionsboxen begrenzen
        if (moveX != 0.0f) {
            int j5 = 0;

            for (int l5 = list1.size(); j5 < l5; ++j5) {
                moveX = list1.get(j5).calculateXOffset(bound, moveX);
            }

            if (moveX != 0.0f) {
                bound.offset(moveX, 0.0f);
            }
        }

        if (moveY != 0.0f) {
            int k5 = 0;

            for (int i6 = list1.size(); k5 < i6; ++k5) {
                moveY = list1.get(k5).calculateYOffset(bound, moveY);
            }

            if (moveY != 0.0f) {
                if (moveY < 0 ^ wantX < 0) {
                    bound.minY += moveY;
                } else {
                    bound.maxY += moveY;
                }
            }
        }
        //Nach Bewegung limitieren Koordinaten vom Collider zurück zu Bound setzen
        player.bound.set(bound.minX, bound.minY, bound.maxX - bound.minX, bound.maxY - bound.minY);
        Borrow.free(list1);
        Borrow.free(bound);
    }

    @Override
    public void draw(int x, int y, Level level) {
        if (game.vars.cubic) {
            game.d.pushMatrix();
            game.d.translate(x + .5f, y + .5f, 0.8f);
            game.d.cube(-.5f, -.5f, 0.1f, 1, 1, .1f, 0xFFB200FF);
            float v = .45f;
            game.d.cube(-v, -v, 0.01f, 2 * v, 2 * v, .1f, 0xFFFF00F2);
            int meta = level.getMeta(x, y);
            v = .4f;
            game.d.lineRect(-v, -v, 2 * v, 2 * v, meta == 1 ? 0xFF00FF00 : 0xFF000000);
            v = .25f;
            game.d.lineRect(-v, -.35f, 2 * v, 2 * v, meta == 0 ? 0xFF00FF00 : 0xFF000000);
            v = .1f;
            game.d.lineRect(-v, -.3f, 2 * v, 2 * v, meta == 2 ? 0xFF00FF00 : 0xFF000000);
            game.d.popMatrix();
        } else {
            game.d.rect(x, y, 1, 1, 0xFFFFFF00);
            game.d.pushMatrix();
            game.d.translate(x + .5f, y + .5f, .9f);
            game.d.rotate(game.vars.inverted_gravity ? -1 : 1, -1, 0, 0);
            game.d.rotate(45, 0, 0, 1);
            final float v = .3f;
            game.d.rect(-v, -v, 2 * v, 2 * v, 0xFF59FF59);
            game.d.popMatrix();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public int metaStates() {
        return 3;
    }
}
