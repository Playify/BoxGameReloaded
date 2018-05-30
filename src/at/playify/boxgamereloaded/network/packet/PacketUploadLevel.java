package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.util.Action;

public class PacketUploadLevel extends Packet {
    private String name;

    public PacketUploadLevel(String s) {
        name=s;
    }

    public PacketUploadLevel() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return name;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        name=s;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (!name.isEmpty()&&name.charAt(0)=='$') {
            game.logger.error(name.substring(1));
        }else{
            game.logger.show(name);
        }
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return name;
    }

    @Override
    public void loadFromString(String s, Server server) {
        name=s;
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient) {
        System.out.println("Player "+connectionToClient.name+" uploads Level named:"+name);
        server.levels.getLevel(connectionToClient.world, new Action<ServerLevel>() {
            @Override
            public void exec(ServerLevel serverLevel) {
                server.levels.upload(connectionToClient.skin.substring(connectionToClient.skin.lastIndexOf(';')+1),connectionToClient.version, name,serverLevel.toWorldString(), new Action<String>() {
                    @Override
                    public void exec(String s) {
                        name=s;
                        connectionToClient.sendPacket(PacketUploadLevel.this);
                    }
                });
            }
        });
    }

    @Override
    public void onSend(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.sendPacket(new PacketSkin(game.player));
    }
}
