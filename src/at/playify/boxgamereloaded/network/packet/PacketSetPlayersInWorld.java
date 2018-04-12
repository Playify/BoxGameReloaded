package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerMP;

import java.util.ArrayList;

public class PacketSetPlayersInWorld extends Packet {
    private ArrayList<String> strings=new ArrayList<>();

    @Override
    public String convertToString(BoxGameReloaded game) {
        return "";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        strings.clear();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c==';'){
                strings.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }else {
                stringBuilder.append(c);
            }
        }
        strings.add(stringBuilder.toString());
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        connectionToServer.players=new Player[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            connectionToServer.players[i]=new PlayerMP(game,strings.get(i));
        }
    }
}
