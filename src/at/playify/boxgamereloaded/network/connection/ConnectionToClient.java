package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.network.packet.Packet;

import java.io.Closeable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//Verbindung zum Client [WIP]
public class ConnectionToClient implements Closeable{
    public Queue<Packet> q=new ConcurrentLinkedQueue<>();
    public void addPendingPacket(Packet packet) {
        q.add(packet);
    }

    public boolean isClosed() {
        return false;
    }

    public void close() {

    }

    public void sendNow(Packet packet) {

    }
}
