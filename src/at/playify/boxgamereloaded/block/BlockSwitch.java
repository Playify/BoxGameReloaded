package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.FakeLevel;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.network.packet.PacketSwitch;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

public class BlockSwitch extends Block implements NoCollideable {
    public static final char chr='m';
    private boolean collided0;

    public BlockSwitch(BoxGameReloaded game, char c){
        super(game, c);
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player){
        list.add(Borrow.bound(x, y, x+1, y+1));
    }

    @Override
    public boolean isBackGround(int meta){
        return false;
    }

    public boolean collide(Bound b, int x, int y, boolean checkOnly, int meta, Level level){
        if (checkOnly) {
            final float v=.01f;
            return b.collide(bound.set(x-v, y, 1+2*v, 1))||b.collide(bound.set(x, y-v, 1, 1+2*v));
        } else {
            return b.collide(bound.set(x, y, 1, 1));
        }
    }

    @Override
    public void draw(int x, int y, Level level){
        game.d.pushMatrix();
        game.d.translate(x, y, 0);
        int meta=game.level.getMeta(x, y);
        if (game.vars.cubic) {
            game.d.cube(0, 0, 0, 1, 1, 1, 0xFF7A63FF);
            for (int i=((level instanceof FakeLevel) ? 4 : 1)-1;i >= 0;i--) {
                if (meta==0) {
                    game.d.cube(.2f, .4f, -.01f, .6f, .2f, .01f, 0xFF000000);
                    game.d.cube(.1f, .3f, -.1f, .8f, .1f, .1f, 0xFFA0A0A0, true, false, true, false);
                    game.d.cube(.1f, .3f, -.1f, .1f, .4f, .1f, 0xFFA0A0A0, false, true, false, true);
                    game.d.cube(.1f, .6f, -.1f, .8f, .1f, .1f, 0xFFA0A0A0, true, false, true, false);
                    game.d.cube(.8f, .3f, -.1f, .1f, .4f, .1f, 0xFFA0A0A0, false, true, false, true);
                    game.d.cube(.2f+(.4f*game.vars.switchState0), .4f, -.15f, .2f, .2f, .15f, Utils.hsvToRgb(game.vars.switchState0/4));
                } else {
                    game.d.cube(.3f, .3f, -.01f, .4f, .4f, .01f, 0xFF000000);
                    game.d.cube(.2f, .2f, -.1f, .6f, .1f, .1f, 0xFFA0A0A0, true, false, true, false);
                    game.d.cube(.2f, .2f, -.1f, .1f, .6f, .1f, 0xFFA0A0A0, false, true, false, true);
                    game.d.cube(.2f, .7f, -.1f, .6f, .1f, .1f, 0xFFA0A0A0, true, false, true, false);
                    game.d.cube(.7f, .2f, -.1f, .1f, .6f, .1f, 0xFFA0A0A0, false, true, false, true);
                    float v=.1f*game.vars.switchState1;
                    game.d.cube(.4f-v, .4f-v, -.15f+v, .2f+.2f*game.vars.switchState1, .2f+.2f*game.vars.switchState1, .15f,
                            Utils.blendColors(game.vars.switchState1, 0xFF00FFFF, 0xFF7F00FF));
                }
                game.d.rotate(90, 0, 1, 0);
                game.d.translate(-1, 0, 0);
            }
        } else {
            float switchState=meta==0 ? game.vars.switchState0 : game.vars.switchState1;
            if (meta==0) {
                game.d.rect(.1f, .3f, .8f, .1f, 0xFFA0A0A0);
                game.d.rect(.1f, .3f, .1f, .4f, 0xFFA0A0A0);
                game.d.rect(.1f, .6f, .8f, .1f, 0xFFA0A0A0);
                game.d.rect(.8f, .3f, .1f, .4f, 0xFFA0A0A0);
                game.d.rect(.2f+(.4f*switchState), .4f, .2f, .2f, 0xFF00FF00);
                game.d.rect(.2f, .4f, .6f, .2f, 0xFF000000);
            } else {
                game.d.rect(.2f, .2f, .6f, .1f, 0xFFA0A0A0);
                game.d.rect(.2f, .2f, .1f, .6f, 0xFFA0A0A0);
                game.d.rect(.2f, .7f, .6f, .1f, 0xFFA0A0A0);
                game.d.rect(.7f, .2f, .1f, .6f, 0xFFA0A0A0);
                float v=.1f*game.vars.switchState1;
                game.d.rect(.4f-v, .4f-v, .2f+.2f*game.vars.switchState1, .2f+.2f*game.vars.switchState1, Utils.blendColors(game.vars.switchState1, 0xFF00FFFF, 0xFF7F00FF));
                game.d.rect(.3f, .3f, .4f, .4f, 0xFF000000);
            }
            game.d.rect(0, 0, 1, 1, 0xFF7A63FF);
        }
        game.d.popMatrix();
    }

    @Override
    public void onNoCollide(PlayerSP player, Level level){
        collided0=false;
        if((game.connection.switchState&2)!=0){
            game.connection.switchState&=~2;
            game.connection.sendPacket(new PacketSwitch(game.connection.switchState));
        }
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data){
        boolean t0=false, t1=false;
        for (int i=0;i<data.size();i++) {
            Borrow.BorrowedCollisionData dat=data.get(i);
            if (dat.blk==this) {
                if (dat.meta==0) {
                    t0=true;
                } else {
                    t1=true;
                }
            }
        }
        boolean send=false;
        if (t0) {
            if (!collided0) {
                collided0=true;
                game.connection.switchState^=1;
                send=true;
            }
        }
        boolean is1=((game.connection.switchState&2)!=0);
        if (t1!=is1){
            game.connection.switchState^=2;
            send=true;
        }
        if (send){
            game.connection.sendPacket(new PacketSwitch(game.connection.switchState));
        }
        return false;
    }

    @Override
    public int metaStates(){
        return 2;
    }
}
