package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.packet.Packet;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

//Verbindung zum SinglePlayer Server [WIP]
public class ConnectionSinglePlayer extends ConnectionToServer implements Closeable {

    private Socket socket;

    public ConnectionSinglePlayer(BoxGameReloaded game) {
        super(game, "192.168.0.123");
    }

    @Override
    public void sendPacket(Packet packet) {
        packet.convertToString(game);
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public void sendNow(Packet packet) {

    }
}
