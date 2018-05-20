package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiMainMenu;
import at.playify.boxgamereloaded.network.LevelHandler;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

import java.util.ArrayList;

public class PacketMainMenu extends Packet {
    public ArrayList<String> list;
    public String name;

    public PacketMainMenu() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return name==null?"":name;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        if (s.isEmpty()) {
            list=null;
            name=null;
        }else{
            StringBuilder str=new StringBuilder();
            char[] chars=s.toCharArray();
            list=new ArrayList<>();
            for (char c : chars) {
                if (c==';') {
                    list.add(str.toString());
                    str.setLength(0);
                } else {
                    str.append(c);
                }
            }
            if (str.length()!=0) {
                list.add(str.toString());
            }
            name=list.remove(0);
        }
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (name==null||list==null) {
            game.gui.openMainMenu();
        }else{
            GuiMainMenu main=game.gui.main;
            if (main!=null) {
                game.levels.put(name,list);
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
    public String convertToString(Server server, ConnectionToClient client) {
        if (name==null||list==null) {
            return "";
        }
        StringBuilder str=new StringBuilder();
        str.append(name);
        for (String s : list) {
            str.append(";").append(s);
        }
        return str.toString();
    }

    @Override
    public void loadFromString(String s, Server server) {
        if (s.isEmpty()) {
            name=null;
        }else{
            name=s;
        }
        list=null;
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient) {
        server.levels.markDirty();
        if (name==null) {
            server.levels.getStages(new LevelHandler.Action<ArrayList<String>>(){

                @Override
                public void exec(ArrayList<String> strings) {
                    name="list";
                    list=strings;
                    connectionToClient.sendPacket(PacketMainMenu.this);
                    for (String s : strings) {
                        final PacketMainMenu packet=new PacketMainMenu();
                        packet.name=s;
                        server.levels.getLevels(s,new LevelHandler.Action<ArrayList<String>>() {
                            @Override
                            public void exec(ArrayList<String> strings) {
                                packet.list=strings;
                                connectionToClient.sendPacket(packet);
                            }
                        });
                    }
                }
            });
        } else {
            server.levels.getLevels(name,new LevelHandler.Action<ArrayList<String>>() {
                @Override
                public void exec(ArrayList<String> strings) {
                    list=strings;
                    connectionToClient.sendPacket(PacketMainMenu.this);
                }
            });
        }
    }
}