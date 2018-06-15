package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;

public class PacketVersion extends Packet {
    public String name;

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        name=game.handler.version();
        connectionToServer.sendPacket(this);
    }
    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.version=name;
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(server.handler.version());
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(game.handler.version());
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        name=in.readString();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        name=in.readString();
    }
}
