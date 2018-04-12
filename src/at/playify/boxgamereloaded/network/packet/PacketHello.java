package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

//Packet beim Joinen
public class PacketHello extends Packet{
    public String name;

    @Override
    public String convertToString(BoxGameReloaded game) {
        if (name==null) {
            return name=game.vars.playername;
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
        game.log("Connected to Server "+name);
    }
}
