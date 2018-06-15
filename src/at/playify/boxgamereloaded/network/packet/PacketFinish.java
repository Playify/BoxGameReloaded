package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.util.Action;

import java.io.IOException;

public class PacketFinish extends Packet {
    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {

    }

    @Override
    public void handle(Server server, final ConnectionToClient connectionToClient) {
        server.levels.getNext(connectionToClient.world,new Action<String>(){

            @Override
            public void exec(String s) {
                connectionToClient.setWorld(s);
            }
        });
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{

    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{

    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{

    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{

    }
}
