package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.packet.PacketMove;
import at.playify.boxgamereloaded.util.Finger;

public class PlayerMP extends Player {
    private boolean hasSkin;
    private String name;
    public PlayerMP(BoxGameReloaded game,String s) {
        super(game);
        this.name=s;
    }

    @Override
    public String skin() {
        if (hasSkin) {
            return super.skin();
        } else {
            return null;
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String name(int data) {
        return "Player:"+name;
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
        bound.setCenter(x, y);
        game.connection.sendPacket(new PacketMove(bound, name));
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public void draw() {
        super.draw();
        if (display!=null&&game.vars.nameTags) {
            game.d.pushMatrix();
            game.d.depth(false);
            game.d.translate(bound.cx(), bound.yh()+bound.h()/7, .5f);
            float strWidth=game.d.getStringWidth(display)*.3f;
            float v=1/8f*.3f;
            game.d.rect(-strWidth/2-v, -v, strWidth+v, .3f+2*v, 0x30000000);
            game.d.drawStringCenter(display, 0, 0, .3f, 0x66000000);
            game.d.translate(0, -bound.h()/10, -.5f);
            game.d.depth(true);
            game.d.popMatrix();
        }
    }
}
