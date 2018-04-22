package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

//Packet beim Joinen
public class PacketHello extends Packet{
    public String name;

    @Override
    public String convertToString(BoxGameReloaded game) {
        if (name==null) {
            return name=game.vars.playerID;
        }
        return name;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        name=s;
        game.connection.connected=true;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.servername=name;
        System.out.println("Connected to Server "+name);
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return name==null?"SERVER":name;
    }

    @Override
    public void loadFromString(String s, Server server) {
        name=s;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        System.out.println("Hello from Client "+name);
        connectionToClient.name=name;
        connectionToClient.sendPacket(new PacketHello());
        connectionToClient.sendPacket(new PacketSetPauseMode(server.getPausemode()));
    }

    @Override
    public void onSend(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.sendPacket(new PacketSkin(game.player));
    }
}
