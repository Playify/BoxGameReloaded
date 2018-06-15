package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;

//Setze Pausezustand
public class PacketServerSettings extends Packet {
    /*
    UNPAUSED   = 0,
    PAUSED     = 1,
    USER       = 2,
    USERPAUSED = 3,
     */
    private int mode;
    private int count;

    public PacketServerSettings(int i) {
        mode=i;
    }

    public PacketServerSettings(){
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.pause = (mode & 1) != 0;
        connectionToServer.userpause=(mode&2)!=0;
        connectionToServer.collide=(mode&4)!=0;
        connectionToServer.pauseCount = count;
        game.pauseLock.unlock();
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        if ((server.getPausemode()&2)!=0) {
            connectionToClient.paused = (mode & 1) != 0;
            server.broadcast(new PacketServerSettings(server.getPausemode()));
        }
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeInt(mode);
        out.writeInt(count);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeInt(mode);
        out.writeInt(count);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        mode=in.readInt();
        count=in.readInt();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        mode=in.readInt();
        count=in.readInt();
    }
}
