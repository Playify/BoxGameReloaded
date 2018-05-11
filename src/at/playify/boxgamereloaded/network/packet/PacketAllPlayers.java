package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

import java.util.ArrayList;

public class PacketAllPlayers extends Packet {
    private ArrayList<String> strings;

    @Override
    public String convertToString(BoxGameReloaded game) {
        return "";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        strings=new ArrayList<>();
        int index=0,last=0;
        while ((index=s.indexOf(';',last))!=-1){
            strings.add(s.substring(last,index));
            last=index+1;
        }
        strings.add(s.substring(last));
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.all=strings;
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        StringBuilder str=new StringBuilder();
        server.checkConnected();
        for (ConnectionToClient connectionToClient : server.getLastBroadcast()) {
            str.append(';').append(connectionToClient.name);
        }
        return str.length()==0?"":str.substring(1);
    }

    @Override
    public void loadFromString(String s, Server server) {
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.sendPacket(this);
    }

    @Override
    public void onSend(Server server, ConnectionToClient connectionToClient) {

        if (server.pauseForSingleUser){
            connectionToClient.sendPacket(new PacketSetPauseMode(server.getPausemode()));
        }
    }
}
