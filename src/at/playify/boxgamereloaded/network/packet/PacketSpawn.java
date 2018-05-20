package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.LevelHandler;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.Arrays;

public class PacketSpawn extends Packet {
    public float x=.1f;
    public float y=.1f;
    public float w=.8f;
    public float h=.8f;

    public PacketSpawn(RectBound bound) {
        x=bound.x();
        y=bound.y();
        w=bound.w();
        h=bound.h();
    }

    @SuppressWarnings("unused")
    public PacketSpawn() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return ((int) (x*100))+";"+((int) (y*100))+";"+((int) (w*100))+";"+((int) (h*100));
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        String[] split=new String[]{"", "", "", ""};
        StringBuilder stringBuilder=new StringBuilder();
        int index=0;
        for (char c : s.toCharArray()) {
            if (c==';') {
                split[index++]=stringBuilder.toString();
                stringBuilder.setLength(0);
                if (index>split.length) {
                    return;
                }
            } else {
                stringBuilder.append(c);
            }
        }
        split[index]=stringBuilder.toString();
        x=Utils.parseInt(split[0], 10)/100f;
        y=Utils.parseInt(split[1], 10)/100f;
        w=Utils.parseInt(split[2], 80)/100f;
        h=Utils.parseInt(split[3], 80)/100f;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.zoom_x=x+w/2;
        game.zoom_y=y+h/2;
        game.level.spawnPoint.set(x, y, w, h);
        game.vars.inverted_gravity=false;
        Arrays.fill(game.vars.keys,false);
        game.vars.check.check(game.level.spawnPoint);
    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return ((int) (x*100))+";"+((int) (y*100))+";"+((int) (w*100))+";"+((int) (h*100));
    }

    @Override
    public void loadFromString(String s, Server server) {
        String[] split=new String[]{"", "", "", ""};
        StringBuilder stringBuilder=new StringBuilder();
        int index=0;
        for (char c : s.toCharArray()) {
            if (c==';') {
                split[index++]=stringBuilder.toString();
                stringBuilder.setLength(0);
                if (index>split.length) {
                    return;
                }
            } else {
                stringBuilder.append(c);
            }
        }
        x=Utils.parseInt(split[0], 10)/100f;
        y=Utils.parseInt(split[1], 10)/100f;
        w=Utils.parseInt(split[2], 80)/100f;
        h=Utils.parseInt(split[3], 80)/100f;
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient) {
        server.levels.getLevel(connectionToClient.world, new LevelHandler.Action<ServerLevel>() {
            @Override
            public void exec(ServerLevel level) {
                RectBound spawnPoint=level.spawnPoint;
                spawnPoint.set(x,y,w,h);
                server.broadcast(new PacketSpawn(spawnPoint),connectionToClient.world,null);
                for (ConnectionToClient client : server.getLastBroadcast()) {
                    client.sendPacket((new PacketMove(spawnPoint)));
                }
            }
        });
    }
}
