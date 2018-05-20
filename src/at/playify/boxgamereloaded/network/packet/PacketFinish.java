package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.LevelHandler;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

public class PacketFinish extends Packet {
    @Override
    public String convertToString(BoxGameReloaded game) {
        return "";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {

    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {

    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return "";
    }

    @Override
    public void loadFromString(String s, Server server) {

    }

    @Override
    public void handle(Server server, final ConnectionToClient connectionToClient) {
        server.levels.getNext(connectionToClient.world,new LevelHandler.Action<String>(){

            @Override
            public void exec(String s) {
                connectionToClient.setWorld(s);
            }
        });
    }
}
