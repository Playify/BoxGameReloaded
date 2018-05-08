package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiMainMenu;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

import java.util.ArrayList;

public class PacketMainMenu extends Packet {
    private ArrayList<String> list;
    private String name;

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
                            main.scroll=.25f*list.indexOf(s);
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
    public void handle(Server server, ConnectionToClient connectionToClient) {
        if (name==null) {
            name="list";
            list=server.getStages();
            connectionToClient.sendPacket(this);
            for (String s : server.getStages()) {
                PacketMainMenu packet=new PacketMainMenu();
                packet.name=s;
                packet.list=server.getLevels(s);
                connectionToClient.sendPacket(packet);
            }
        } else {
            list=server.getLevels(name);
            connectionToClient.sendPacket(this);
        }
    }
}