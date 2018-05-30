package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

public class PacketKick extends Packet {
    private String msg;

    public PacketKick(String reason) {
        msg=reason==null?"":reason;
    }
    public PacketKick() {
        msg="";
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return msg;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        msg=s;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.close();
        game.logger.show("Kicked from Server (Reason:"+msg+")");
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return msg;
    }

    @Override
    public void loadFromString(String s, Server server) {
        msg=s;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        System.out.println("Player "+connectionToClient.name+" left the Game (Reason:"+msg+")");
        connectionToClient.close();
    }
}
