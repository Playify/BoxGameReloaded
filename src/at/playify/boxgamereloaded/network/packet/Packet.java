package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

//Packets die vom Server zum Client oder umgekehr versendet werden.
public abstract class Packet {

    public abstract String convertToString(BoxGameReloaded game);

    public abstract void loadFromString(String s,BoxGameReloaded game);

    public abstract void handle(BoxGameReloaded game, ConnectionToServer connectionToServer);


    public abstract String convertToString(Server server, ConnectionToClient client);
    public abstract void loadFromString(String s,Server server);
    public abstract void handle(Server server, ConnectionToClient connectionToClient);
    public void onSend(Server server, ConnectionToClient connectionToClient){}
    @SuppressWarnings("unused")
    void nopermission(Server server, ConnectionToClient connectionToClient) {
        System.out.println("No Permission");
    }
}
