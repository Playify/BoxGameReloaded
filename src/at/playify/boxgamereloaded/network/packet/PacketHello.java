package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;

//Packet beim Joinen
public class PacketHello extends Packet {
    public String name;

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.servername=name;
        game.logger.show("Connected to Server "+name);
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        System.out.println("Hello from Client "+name);
        if (server.getByName(name)!=null) {
            connectionToClient.name=name;
            connectionToClient.close("Already Connected");
        }else {
            connectionToClient.name=name;
            connectionToClient.sendPacket(new PacketHello());
            connectionToClient.sendPacket(new PacketServerSettings(server.getPausemode()));
        }
    }

    @Override
    public void onSend(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.sendPacket(new PacketSetWorld(game.vars.world));
        connectionToServer.sendPacket(new PacketSkin(game.player));
        connectionToServer.sendPacket(new PacketVersion());
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(name==null?"SERVER":name);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(name==null?game.vars.playerID:name);
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
