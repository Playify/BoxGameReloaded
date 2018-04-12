package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.util.bound.RectBound;
import at.playify.boxgamereloaded.util.Utils;

//Packet um den Client von Spielerbewegungen zu berichten und den Server von Clientbewegungen
//Noch nicht getestet
public class PacketMove extends Packet {
    public String player="";
    public float x=.1f;
    public float y=.1f;
    public float w=.8f;
    public float h=.8f;

    public PacketMove(RectBound bound) {
        player="";
        x=bound.x();
        y=bound.y();
        w=bound.w();
        h=bound.h();
    }
    public PacketMove() {
        player="";
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return ((int)(x*100))+";"+((int)(y*100))+";"+((int)(w*100))+";"+((int)(h*100))+";"+player;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        String[] split=new String[]{"","","","",""};
        StringBuilder stringBuilder = new StringBuilder();
        int index=0;
        for (char c : s.toCharArray()) {
            if (c==';'){
                split[index++]=stringBuilder.toString();
                stringBuilder.setLength(0);
                if (index>split.length) {
                    return;
                }
            }else {
                stringBuilder.append(c);
            }
        }
        split[index]=stringBuilder.toString();
        x= Utils.parseInt(split[0],10)/100f;
        y= Utils.parseInt(split[1],10)/100f;
        w= Utils.parseInt(split[2],80)/100f;
        h= Utils.parseInt(split[3],80)/100f;
        player=split[4];
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        if (player==null||player.isEmpty()||player.equals(game.vars.playername)){
            game.player.motionX=game.player.motionY=0;
            game.player.bound.set(x,y,w,h);
            connectionToServer.serverbound.set(x,y,w,h);
        }else{
            Player[] p=connectionToServer.players;
            for(Player player1 : p) {
                if (player1.name().equals(player)) {
                    player1.bound.set(x,y,w,h);
                }
            }
        }
    }
}
