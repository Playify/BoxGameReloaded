package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.player.PlayerMP;

public class PacketResetPlayersInWorld extends Packet {
    public String player;

    @SuppressWarnings("WeakerAccess")
    public PacketResetPlayersInWorld(String name) {
        player=name;
    }
    @SuppressWarnings("WeakerAccess")
    public PacketResetPlayersInWorld() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return player==null?"":player;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        player=s.isEmpty()?null:s;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        synchronized (connectionToServer.playerLock){
            if (player==null) {
                connectionToServer.players= new PlayerMP[0];
            }else{
                PlayerMP[] players = connectionToServer.players;
                boolean rem=false;
                for (PlayerMP playerMP : players) {
                    if (player.equals(playerMP.name())) {
                        rem=true;
                    }
                }
                if (rem) {
                    PlayerMP[] now = new PlayerMP[players.length - 1];
                    int i = 0;
                    for (PlayerMP p : players) {
                        if (!player.equals(p.name())) {
                            now[i] = p;
                            i++;
                        }
                    }
                    connectionToServer.players = now;
                }
            }
        }
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return player==null ? "" : player;
    }

    @Override
    public void loadFromString(String s, Server server) {
        player=s.isEmpty()?null:s;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
    }
}
