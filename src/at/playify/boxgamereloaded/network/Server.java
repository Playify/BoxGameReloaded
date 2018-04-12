package at.playify.boxgamereloaded.network;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.packet.Packet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

//Server wird in extrigem Projekt ausgeführt und erst später ins Spiel implementiert
public class Server {
    public ArrayList<ConnectionToClient> cons=new ArrayList<>();

    public void tick() {
        Iterator<ConnectionToClient> iterator=cons.iterator();
        while (iterator.hasNext()) {
            ConnectionToClient next=iterator.next();
            if (next.isClosed()) {
                iterator.remove();
                next.close();
            }else{
                Queue<Packet> q=next.q;
                while (!q.isEmpty()){
                    Packet remove=q.remove();
                    next.sendNow(remove);
                }
            }
        }
    }

    public Level getLevel(String world) {
        return null;
    }
}
