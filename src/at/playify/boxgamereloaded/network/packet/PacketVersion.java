package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

public class PacketVersion extends Packet {
    public String name;

    @Override
    public String convertToString(BoxGameReloaded game) {
        return game.handler.version();
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        name=s;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        name=game.handler.version();
        connectionToServer.sendPacket(this);
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return server.handler.version();
    }

    @Override
    public void loadFromString(String s, Server server) {
        name=s;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.version=name;
    }
}
