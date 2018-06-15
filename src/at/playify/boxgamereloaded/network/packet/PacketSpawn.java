package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.util.Action;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.io.IOException;
import java.util.Arrays;

public class PacketSpawn extends Packet {
    public float x=.1f;
    public float y=.1f;
    public float w=.8f;
    public float h=.8f;

    public PacketSpawn(){
    }

    public PacketSpawn(RectBound bound) {
        x=bound.x();
        y=bound.y();
        w=bound.w();
        h=bound.h();

    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.zoom_x=x+w/2;
        game.zoom_y=y+h/2;
        game.level.spawnPoint.set(x, y, w, h);
        game.vars.gravity=false;
        Arrays.fill(game.vars.keys,false);
        game.vars.check.check(game.level.spawnPoint);
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient) {
        server.levels.getLevel(connectionToClient.world, new Action<ServerLevel>() {
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

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(w);
        out.writeFloat(h);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(w);
        out.writeFloat(h);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        x=in.readFloat();
        y=in.readFloat();
        w=in.readFloat();
        h=in.readFloat();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        x=in.readFloat();
        y=in.readFloat();
        w=in.readFloat();
        h=in.readFloat();
    }
}
