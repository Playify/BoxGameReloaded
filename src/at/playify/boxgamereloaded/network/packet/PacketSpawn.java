package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class PacketSpawn extends Packet {
    public float x=.1f;
    public float y=.1f;
    public float w=.8f;
    public float h=.8f;

    @SuppressWarnings("unused")
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
    public void handle(Server server, ConnectionToClient connectionToClient) {
        nopermission(server, connectionToClient);
    }
}
