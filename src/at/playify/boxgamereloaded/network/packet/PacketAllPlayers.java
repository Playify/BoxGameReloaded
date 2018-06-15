package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;
import java.util.ArrayList;

public class PacketAllPlayers extends Packet {
    private ArrayList<String> strings;

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.all=strings;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.sendPacket(this);
    }

    @Override
    public void onSend(Server server, ConnectionToClient connectionToClient) {
        if (server.pauseForSingleUser){
            connectionToClient.sendPacket(new PacketServerSettings(server.getPausemode()));
        }
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        server.checkConnected();
        ArrayList<ConnectionToClient> list=server.getLastBroadcast();
        strings=new ArrayList<>(list.size());
        for (int i=0;i<list.size();i++) {
            String name=list.get(i).name;
            strings.add(name==null?"":name);
        }
        if (strings==null)out.writeInt(-1);
        else{
            out.writeInt(strings.size());
            for (int i=0;i<strings.size();i++) {
                out.writeString(strings.get(i));
            }
        }
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        if (strings==null)out.writeInt(-1);
        else{
            out.writeInt(strings.size());
            for (int i=0;i<strings.size();i++) {
                out.writeString(strings.get(i));
            }
        }


    }   @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        int size=in.readInt();
        if (size==-1)strings=null;
        else{
            strings=new ArrayList<>(size);
            for (int i=0;i<size;i++) {
                strings.add(in.readString());
            }
        }
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{

        int size=in.readInt();
        if (size==-1)strings=null;
        else{
            strings=new ArrayList<>(size);
            for (int i=0;i<size;i++) {
                strings.add(in.readString());
            }
        }
    }
}
