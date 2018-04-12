package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

//Packet um die Welt zu setzen
public class PacketSetWorld extends Packet {
    public String world;
    public PacketSetWorld(String world) {
        this.world=world;
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return world;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        world=s;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.vars.world=world;
    }

}
