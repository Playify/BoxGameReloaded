package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.player.PlayerMP;

public class PacketResetPlayersInWorld extends Packet {
    @Override
    public String convertToString(BoxGameReloaded game) {
        return "";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        synchronized (connectionToServer.playerLock){
            connectionToServer.players= new PlayerMP[0];
        }
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return "";
    }

    @Override
    public void loadFromString(String s, Server server) {

    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {

    }
}
