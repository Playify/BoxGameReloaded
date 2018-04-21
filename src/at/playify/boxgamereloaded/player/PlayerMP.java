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
}
