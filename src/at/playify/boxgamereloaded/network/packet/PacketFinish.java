package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PacketFinish extends Packet {
    @Override
    public String convertToString(BoxGameReloaded game) {
        return "";
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {

    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {

    }

    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return "";
    }

    @Override
    public void loadFromString(String s, Server server) {

    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        JSONObject obj=connectionToClient.world.startsWith("paint") ?server.handler.read("paint"):server.handler.assetJson( "leveldata");
        try {
            if (obj.has(connectionToClient.world)) {
                JSONObject lvl=obj.getJSONObject(connectionToClient.world);
                if (lvl.has("next")) {
                    System.out.println(lvl.getString("next"));
                    connectionToClient.setWorld(lvl.getString("next"));
                    return;
                }
            }
            Pattern compile=Pattern.compile("([a-zA-Z]*)([0-9]+)");
            Matcher matcher=compile.matcher(connectionToClient.world);
            if (matcher.matches()) {
                String pre=matcher.group(1);
                String w=pre+(Integer.parseInt(matcher.group(2))+1);
                if(!server.isLevel(w)){
                    w=pre+"0";
                }
                System.out.println(w);
                connectionToClient.setWorld(w);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
