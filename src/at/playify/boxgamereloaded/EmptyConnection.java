package at.playify.boxgamereloaded;

import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.packet.Packet;

//Verbindung ohne Packets zu senden/empfangen
public class EmptyConnection extends ConnectionToServer {

    EmptyConnection() {
    }

    @Override
    public void sendPacket(Packet packet) {
    }

    public void sendPacketSoon(Packet packet){
    }

    @Override
    public void leaveWorld() {
        Game.report(new Exception("TEST"));
    }
}
