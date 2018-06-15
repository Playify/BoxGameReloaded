package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.player.PlayerMP;

import java.io.IOException;

public class PacketResetPlayersInWorld extends Packet {
    public String player;

    public PacketResetPlayersInWorld(String name) {
        player=name;
    }
    public PacketResetPlayersInWorld() {
        player="";
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        synchronized (connectionToServer.playerLock){
            if (player.isEmpty()) {
                connectionToServer.players= new PlayerMP[0];
                game.level.clearHistory();
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
    public void handle(Server server, ConnectionToClient connectionToClient) {
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(player);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(player);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        player=in.readString();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        player=in.readString();
    }
}
