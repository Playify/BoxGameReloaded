package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;

//Packets die vom Server zum Client oder umgekehr versendet werden.
public abstract class Packet {

    public abstract String convertToString(BoxGameReloaded game);

    public abstract void loadFromString(String s,BoxGameReloaded game);

    public abstract void handle(BoxGameReloaded game, ConnectionToServer connectionToServer);
}
