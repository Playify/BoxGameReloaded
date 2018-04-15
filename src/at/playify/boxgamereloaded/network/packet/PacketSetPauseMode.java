package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.util.Utils;

//Setze Pausezustand
public class PacketSetPauseMode extends Packet {
    /*
    UNPAUSED   = 0,
    PAUSED     = 1,
    USER       = 2,
    USERPAUSED = 3,
     */
    private int mode;
    private int count;

    public PacketSetPauseMode(int i) {
        mode=i;
    }
    @SuppressWarnings("unused")
    public PacketSetPauseMode() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return mode + "";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        mode = s.charAt(0) - '0';
        count = Utils.parseInt(s.substring(1), 0);
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.pause = (mode & 1) != 0;
        connectionToServer.userpause=(mode&2)!=0;
        connectionToServer.pauseCount = count;
        game.pauseLock.unlock();
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        int count = 0;
        server.checkConnected();
        for (ConnectionToClient connectionToClient : server.getLastBroadcast()) {
            if (connectionToClient.paused) {
                count++;
            }
        }
        if (count == 1 && client.paused) {
            count = 0;
        }
        return mode + "" + count;
    }

    @Override
    public void loadFromString(String s, Server server) {
        mode = s.charAt(0) - '0';
        count = 0;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        if ((server.getPausemode()&2)!=0) {
            connectionToClient.paused = (mode & 1) != 0;
            server.broadcast(new PacketSetPauseMode(server.getPausemode()));
        }else{
            nopermission(server,connectionToClient);
        }
    }
}
