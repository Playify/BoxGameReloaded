package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

//Packet für Leveldaten = Level zu Text,Laden von Text
public class PacketLevelData extends Packet {
    private String levelstr;
    private String world;

    public PacketLevelData(String levelstr) {
        this.levelstr=levelstr;
    }
    public PacketLevelData() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return (world==null?game.vars.world:world)+";"+(levelstr==null?game.level.toWorldString():levelstr);
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        int i=s.indexOf(';');
        world=i==-1?null:s.substring(0,i);
        levelstr=s.substring(i+1);
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.level.loadWorldString(levelstr);
    }
}
