package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.util.Utils;

//Setze Pausezustand
public class PacketSetPauseMode extends Packet {
    public static final int UNPAUSED = 0;
    public static final int PAUSED = 1;
    public static final int USER=2;
    public int mode;

    public PacketSetPauseMode(int i) {
        mode=i;
    }
    public PacketSetPauseMode() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return mode+"";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        this.mode=Utils.parseInt(s,PacketSetPauseMode.UNPAUSED);
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.pause=(mode&1)!=0&&!game.paused;
        connectionToServer.userpause=(mode&2)!=0;
        game.pauseLock.unlock();
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return mode+"";
    }

    @Override
    public void loadFromString(String s, Server server) {
        mode= Utils.parseInt(s,0);
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        if ((server.getPausemode()&2)!=0) {
            server.setPausemode(2|(mode&1));
            server.broadcast(new PacketSetPauseMode(server.getPausemode()),connectionToClient);
        }else{
            nopermission(server,connectionToClient);
        }
    }
}
