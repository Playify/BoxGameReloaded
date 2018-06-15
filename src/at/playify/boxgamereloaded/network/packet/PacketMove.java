package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.player.PlayerMP;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.io.IOException;
import java.util.ArrayList;

//Packet um den Client von Spielerbewegungen zu berichten und den Server von Clientbewegungen
public class PacketMove extends Packet {
    public String player="";
    public float x=.1f;
    public float y=.1f;
    public float w=.8f;
    public float h=.8f;

    public PacketMove(ConnectionToClient client) {
        this.player=client.name==null?"":client.name;
        x=client.bound.x();
        y=client.bound.y();
        w=client.bound.w();
        h=client.bound.h();
    }
    public PacketMove(RectBound bound, String player) {
        this.player=player==null?"":player;
        x=bound.x();
        y=bound.y();
        w=bound.w();
        h=bound.h();
    }
    public PacketMove(float x,float y,float w,float h,String player) {
        this.player=player==null?"":player;
        this.x=x;
        this.y=y;
        this.w=w;
        this.h=h;
    }
    public PacketMove(RectBound bound) {
        player="";
        x=bound.x();
        y=bound.y();
        w=bound.w();
        h=bound.h();
    }

    public PacketMove(){
        player="";
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (player==null||player.isEmpty()||player.equals(game.vars.playerID)) {
            synchronized (game.player.bound) {
                game.player.motionX = game.player.motionY = 0;
                game.player.bound.set(x, y, w, h);
                connectionToServer.serverbound.set(x, y, w, h);
            }
        }else{
            PlayerMP[] p=connectionToServer.players;
            for (PlayerMP playerMP : p) {
                if (playerMP.name().equals(player)) {
                    playerMP.bound.set(x, y, w, h);
                    return;
                }
            }
            synchronized (connectionToServer.playerLock){
                int length = connectionToServer.players.length;
                PlayerMP[] players = new PlayerMP[length + 1];
                System.arraycopy(connectionToServer.players, 0, players, 0, length);
                PlayerMP playerMP = new PlayerMP(game, player);
                playerMP.bound.set(x,y,w,h);
                players[players.length - 1]= playerMP;
                connectionToServer.players= players;
            }
            connectionToServer.sendPacket(new PacketSkin(player, null));
        }
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        if (player==null||player.isEmpty()||player.equals(connectionToClient.name)) {
            player=connectionToClient.name;
            if ((server.pausemode & 4) != 0) {
                ArrayList<ConnectionToClient> list=new ArrayList<>();
                server.getByWorld(connectionToClient.world, list);
                for (int i=list.size()-1;i >= 0;i--) {
                    RectBound bound=list.get(i).bound;
                    if (!bound.collide(connectionToClient.bound)) {
                        if (bound.collide(connectionToClient.collider.set(x, y, w, h))) {
                            System.out.println("Moved back Player:"+connectionToClient.name);
                            connectionToClient.sendPacket(new PacketMove(connectionToClient.bound,connectionToClient.name));
                            return;
                        }
                    }
                }
            }
            connectionToClient.bound.set(x,y,w,h);
            server.broadcast(this,connectionToClient.world,connectionToClient);
        } else {//Draw other players
            server.checkConnected();
            for (ConnectionToClient con : server.getLastBroadcast()) {
                if (con.world.equals(connectionToClient.world)&&con.name.equals(player)) {
                    con.bound.set(x, y, w, h);
                }
            }
            server.broadcast(this, connectionToClient.world, connectionToClient);
        }
    }

    @Override
    public void onSend(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (player==null||player.isEmpty()||player.equals(game.vars.playerID)) {
            connectionToServer.serverbound.set(x,y,w,h);
        }
    }

    @Override
    public void onSend(Server server, ConnectionToClient connectionToClient) {
        if (player==null||player.isEmpty()||player.equals(connectionToClient.name)) {
            connectionToClient.bound.set(x,y,w,h);
        }
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(player);
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(w);
        out.writeFloat(h);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(player);
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(w);
        out.writeFloat(h);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        player=in.readString();
        x=in.readFloat();
        y=in.readFloat();
        w=in.readFloat();
        h=in.readFloat();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        player=in.readString();
        x=in.readFloat();
        y=in.readFloat();
        w=in.readFloat();
        h=in.readFloat();
    }
}
