package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

import java.util.ArrayList;

//Packet um die Welt zu setzen
public class PacketSetWorld extends Packet {
    private String world;
    public PacketSetWorld(String world) {
        this.world=world;
    }
    public PacketSetWorld() {
    }

    @Override
    public String convertToString(BoxGameReloaded game) {
        return world;
    }

    @Override
    public void loadFromString(String s, BoxGameReloaded game) {
        world=s;
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.vars.world=world;
    }


    @Override
    public String convertToString(Server server, ConnectionToClient client) {
        return world;
    }

    @Override
    public void loadFromString(String s, Server server) {
        world=s;
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        PacketResetPlayersInWorld packet=new PacketResetPlayersInWorld(connectionToClient.name);
        server.broadcast(packet,connectionToClient.world,connectionToClient);
        ServerLevel level = server.getLevel(world);
        if (!connectionToClient.world.equals(world)) {
            connectionToClient.world=world;
            connectionToClient.sendPacket(new PacketMove(level.spawnPoint,connectionToClient.name));

        }
        connectionToClient.sendPacket(new PacketLevelData(level.toWorldString()));
        connectionToClient.sendPacket(new PacketResetPlayersInWorld());
        PacketMove packetMove = new PacketMove(connectionToClient);
        server.broadcast(packetMove,world,connectionToClient);
        ArrayList<ConnectionToClient> last = server.getLastBroadcast();
        for (ConnectionToClient client : last) {
            connectionToClient.sendPacket(new PacketMove(client));
        }

    }

    @Override
    public void onSend(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.world=world;
        System.out.println(connectionToClient.name+" moved in world: "+world);
    }
}
