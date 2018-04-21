package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerMP;

public class PacketSkin extends Packet {

    private String player;
    private String skin;

    @SuppressWarnings("unused")
    public PacketSkin(Player p) {
        this.player=p.name();
        this.skin=p.skin();
    }

    @SuppressWarnings("unused")
    public PacketSkin() {
    }

    @SuppressWarnings("WeakerAccess")
    public PacketSkin(String player, String skin) {
        this.player=player;
        this.skin=skin;
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        if (skin==null) {
            return player;
        }
        return player+';'+skin;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        int ind=s.indexOf(';');
        if (ind==-1) {
            player=s;
            skin=null;
        } else {
            player=s.substring(0, ind);
            skin=s.substring(ind+1);
        }
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (skin!=null) {
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
    public String convertToString(Server server, ConnectionToClient client) {
        if (skin==null) {
            return player;
        }
        return player+';'+skin;
    }

    @Override
    public void loadFromString(String s, Server server) {
        int ind=s.indexOf(';');
        if (ind==-1) {
            player=s;
            skin=null;
        } else {
            player=s.substring(0, ind);
            skin=s.substring(ind+1);
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
}
