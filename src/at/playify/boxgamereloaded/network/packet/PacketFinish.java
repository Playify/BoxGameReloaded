package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import org.json.JSONObject;

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
        JSONObject obj=server.handler.read(connectionToClient.world.startsWith("paint_") ? "paint" : "levels");
        if (obj.has(connectionToClient.world)) {
            JSONObject lvl=obj.getJSONObject(connectionToClient.world);
            if (lvl.has("next")) {
                connectionToClient.setWorld(lvl.getString("next"));
            }
        }
    }
}
