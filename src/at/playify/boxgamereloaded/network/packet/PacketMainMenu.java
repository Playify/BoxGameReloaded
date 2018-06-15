package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiMainMenu;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.util.Action;

import java.io.IOException;
import java.util.ArrayList;

public class PacketMainMenu extends Packet {
    public ArrayList<String> list;
    public String name;

    public PacketMainMenu(){
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer){
        if (name.isEmpty()||list==null) {
            game.gui.openMainMenu();
        } else {
            GuiMainMenu main=game.gui.main;
            if (main!=null) {
                game.levels.put(name, list);
                if (game.vars.stage==null) {
                    if (!name.equals("list")) {
                        game.vars.stage=name;
                    }
                }
                if (name.equals(game.vars.stage)) {
                    for (String s : list) {
                        String lvl=s.substring(0, s.indexOf('='));
                        if (lvl.equals(game.vars.lastWorld)) {
                            main.scroller.setScroll(.25f*list.indexOf(s));
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient){
        server.levels.markDirty();
        if (name==null) {
            server.levels.getStages(new Action<ArrayList<String>>() {

                @Override
                public void exec(ArrayList<String> strings){
                    name="list";
                    list=strings;
                    connectionToClient.sendPacket(PacketMainMenu.this);
                    for (String s : strings) {
                        final PacketMainMenu packet=new PacketMainMenu();
                        packet.name=s;
                        server.levels.getLevels(s, new Action<ArrayList<String>>() {
                            @Override
                            public void exec(ArrayList<String> strings){
                                packet.list=strings;
                                connectionToClient.sendPacket(packet);
                            }
                        });
                    }
                }
            });
        } else {
            server.levels.getLevels(name, new Action<ArrayList<String>>() {
                @Override
                public void exec(ArrayList<String> strings){
                    list=strings;
                    connectionToClient.sendPacket(PacketMainMenu.this);
                }
            });
        }
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(name==null ? "" : name);
        if (list==null) out.writeInt(-1);
        else{
            out.writeInt(list.size());
            for (int i=0;i<list.size();i++) {
                out.writeString(list.get(i));
            }
        }
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(name==null ? "" : name);
        if (list==null) out.writeInt(-1);
        else{
            out.writeInt(list.size());
            for (int i=0;i<list.size();i++) {
                out.writeString(list.get(i));
            }
        }
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        String s=in.readString();
        name=s.isEmpty()?null:s;
        int size=in.readInt();
        if (size==-1) {
            list=null;
        }else{
            list=new ArrayList<>(size);
            for (int i=0;i<size;i++) {
                list.add(in.readString());
            }
        }
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        String s=in.readString();
        name=s.isEmpty()?null:s;
        int size=in.readInt();
        if (size==-1) {
            list=null;
        }else{
            list=new ArrayList<>(size);
            for (int i=0;i<size;i++) {
                list.add(in.readString());
            }
        }
    }
}