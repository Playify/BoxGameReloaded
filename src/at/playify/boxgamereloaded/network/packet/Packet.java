package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;

import java.io.IOException;

//Packets die vom Server zum Client oder umgekehr versendet werden.
public abstract class Packet {

    public abstract void handle(BoxGameReloaded game, ConnectionToServer connectionToServer);
    public abstract void handle(Server server, ConnectionToClient connectionToClient);

    public void onSend(Server server, ConnectionToClient con){}
    public void onSend(BoxGameReloaded game, ConnectionToServer con) {}

    public abstract void send(Output out, Server server, ConnectionToClient con) throws IOException;
    public abstract void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException;

    public abstract void receive(Input in, Server server, ConnectionToClient con) throws IOException;
    public abstract void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException;
}
