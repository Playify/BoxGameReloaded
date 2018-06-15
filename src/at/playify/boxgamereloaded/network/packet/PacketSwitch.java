package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;
import java.util.ArrayList;

public class PacketSwitch extends Packet {
    private int val;

    public PacketSwitch(int switchState){
        this.val=switchState;
    }

    public PacketSwitch(){
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer){
        connectionToServer.switch0=(val&1)!=0;
        connectionToServer.switch1=(val&2)!=0;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient){
        System.out.println("Received:"+val);
        connectionToClient.switchState=val;
        ArrayList<ConnectionToClient> list=new ArrayList<>();
        server.getByWorld(connectionToClient.world,list);
        int switchState=val;
        for (int i=0;i<list.size();i++) {
            switchState|=list.get(i).switchState;
        }
        val=switchState;
        server.broadcast(this,connectionToClient.world,null);
    }
    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeInt(val);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeInt(val);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        val=in.readInt();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        val=in.readInt();
    }
}
