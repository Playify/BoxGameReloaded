package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;

//Packet um die Welt zu setzen
public class PacketSetWorld extends Packet {
    private String world;
    public PacketSetWorld(String world) {
        this.world=world;
    }

    public PacketSetWorld(){
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {
        game.vars.world=world;
        if (world.equals("Lobby")){
            if (!game.gui.isMainMenuVisible()) game.gui.openMainMenu();
        }else if (!world.equals("NONE")) {
            if (game.gui.isMainMenuVisible()) {
                game.gui.closeMainMenu();
                game.paused=false;
                game.pauseLock.unlock();
            }
            game.vars.lastWorld=world;
        }
    }

    @Override
    public void handle(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.setWorld(world);
    }

    @Override
    public void onSend(Server server, ConnectionToClient connectionToClient) {
        connectionToClient.world=world;
        System.out.println(connectionToClient.name+" moved in world: "+world);
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeString(world);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeString(world);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        world=in.readString();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        world=in.readString();
    }
}
