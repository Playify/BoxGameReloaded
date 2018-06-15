package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerMP;

import java.io.IOException;

public class PacketSkin extends Packet {

    private String player;
    private String skin;

    public PacketSkin(Player p) {
        this.player=p.name();
        this.skin=p.skin();
    }

    public PacketSkin(String player, String skin) {
        this.player=player;
        this.skin=skin;
    }
    public PacketSkin(){
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (!skin.isEmpty()) {
            PlayerMP[] players=connectionToServer.players;
            Player p=null;
            if (player==null||player.isEmpty()||player.equals(game.player.name())) {
                p=game.player;
            } else {
                for (PlayerMP playerMP : players) {
                    if (player.equals(playerMP.name())) {
                        p=playerMP;
                    }
                }
            }
            if (p!=null) {
                p.skin(skin);
            }
        }
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        if (player==null||player.isEmpty()||player.equals(connectionToClient.name)) {
            connectionToClient.skin=skin;
            player=connectionToClient.name;
            server.broadcast(this, connectionToClient.world, connectionToClient);
        } else {
            ConnectionToClient con=server.getByName(player);
            if (con!=null) {
                connectionToClient.sendPacket(new PacketSkin(player, con.skin));
            }
        }
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(player);
        out.writeString(skin==null?"":skin);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(player);
        out.writeString(skin==null?"":skin);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        player=in.readString();
        skin=in.readString();
    }


    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        player=in.readString();
        skin=in.readString();

    }
}
