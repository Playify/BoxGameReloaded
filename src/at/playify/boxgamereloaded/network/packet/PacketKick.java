package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;

public class PacketKick extends Packet {
    private String msg;

    public PacketKick(String reason) {
        msg=reason==null?"":reason;
    }
    public PacketKick() {
        msg="";
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.close();
        game.logger.show("Kicked from Server (Reason:"+msg+")");
    }
    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        System.out.println("Player "+connectionToClient.name+" left the Game (Reason:"+msg+")");
        connectionToClient.close();
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(msg);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(msg);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        msg=in.readString();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        msg=in.readString();
    }
}
