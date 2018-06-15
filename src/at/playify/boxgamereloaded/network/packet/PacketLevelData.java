package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.util.Action;
import at.playify.boxgamereloaded.util.json.JSONException;
import at.playify.boxgamereloaded.util.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

//Packet für Leveldaten = Level zu Text,Laden von Text
public class PacketLevelData extends Packet {
    private String levelstr;
    private String world;

    public PacketLevelData(String levelstr) {
        this.levelstr=levelstr;
    }
    @SuppressWarnings("unused")
    public PacketLevelData() {
    }
    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.level.loadWorldString(levelstr);
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient) {
        final String world=this.world.isEmpty() ? connectionToClient.world : this.world;
        if (world.startsWith("paint")) {
            try {
                server.levels.getLevel(world, new Action<ServerLevel>() {
                    @Override
                    public void exec(ServerLevel level) {
                        try {
                            JSONObject paint=server.handler.read("paint");
                            if (!paint.has(world)) {
                                paint.put(world, new JSONObject());
                            }
                            JSONObject lvl=paint.getJSONObject(world);
                            lvl.put("data", levelstr);
                            if (connectionToClient.skin!=null) {
                                lvl.put("by", connectionToClient.skin.substring(connectionToClient.skin.lastIndexOf(';')+1));
                                final PacketMainMenu packet=new PacketMainMenu();

                                server.levels.getLevels("paint", new Action<ArrayList<String>>() {
                                    @Override
                                    public void exec(ArrayList<String> strings) {
                                        packet.name="paint";
                                        packet.list=strings;
                                        server.broadcast(packet);
                                    }
                                });
                            }
                            server.handler.write("paint", paint);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        level.loadWorldString(levelstr);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        server.broadcast(this, world, connectionToClient);
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(world==null?"":world);
        out.writeString(levelstr);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(world==null?"":world);
        out.writeString(levelstr);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        world=in.readString();
        levelstr=in.readString();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        world=in.readString();
        levelstr=in.readString();
    }
}
