package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.packet.*;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

//Verbindung zum Client
public class ConnectionToClient extends Thread implements Closeable{
    private final Server server;
    private Socket socket;
    private BufferedReader in;
    private PrintStream out;
    private boolean closed;
    public String world = "NULL";
    public String name;
    public RectBound bound = new RectBound(.1f, 0, .8f, .8f);
    public boolean paused;

    public ConnectionToClient(Socket socket, Server server) {
        this.server = server;
        try {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            closed = true;
            return;
        }
        setName("ConnectionToClient");
        start();
        System.out.println("Opened Connection to " + socket.getInetAddress());
    }

    public ConnectionToClient(InputStream in, OutputStream out, Server server) {
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new PrintStream(out);
        setName("ConnectionToSinglePlayer");
        start();
        System.out.println("Opened Connection to SinglePlayer");
    }

    public void sendPacket(Packet packet) {
        out.println(packet.getClass().getSimpleName().substring(6) + ":" + packet.convertToString(server, this));
        if (out.checkError()) {
            close();
        }
        packet.onSend(server,this);
    }

    public void sendRaw(String s) {
        out.println(s);
        if (out.checkError()) {
            close();
        }
    }


    public boolean isClosed() {
        return closed || (socket != null && socket.isClosed());
    }


    @Override
    public void close() {
        try {
            if (socket!=null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            server.broadcast(new PacketResetPlayersInWorld(name),world,this);
            if (paused) {
                paused = false;
                server.broadcast(new PacketSetPauseMode(server.getPausemode()), this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        server.disconnect(this);
    }

    @Override
    public void run() {
        try {
            String s;
            while ((s = in.readLine()) != null) {
                try {
                    int i = s.indexOf(':');
                    String packetName = i == -1 ? s : s.substring(0, i);
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends Packet> cls = (Class<? extends Packet>) Class.forName(Packet.class.getName() + packetName);
                        Packet packet = cls.newInstance();
                        packet.loadFromString(i == -1 ? "" : s.substring(i + 1), server);
                        packet.handle(server, this);
                    } catch (ClassNotFoundException cls) {
                        System.err.println("Unknown Packet received: " + packetName);
                    } catch (ClassCastException cls) {
                        System.err.println("Wrong Packet received: " + packetName);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (SocketException e) {
            System.err.println("Connection lost to Player \""+name+"\" Reason: "+e.getMessage());
            close();
        }catch (Exception e) {
            System.err.println("Error in ConnectionToClient");
            e.printStackTrace();
            close();
        }
    }

    public void setWorld(String w) {
        PacketResetPlayersInWorld packet=new PacketResetPlayersInWorld(this.name);
        server.broadcast(packet, w, this);
        ServerLevel level=server.getLevel(w);
        if (!this.world.equals(w)) {
            this.world=w;
            this.sendPacket(new PacketMove(level.spawnPoint, this.name));

        }
        this.sendPacket(new PacketLevelData(level.toWorldString()));
        this.sendPacket(new PacketResetPlayersInWorld());
        PacketMove packetMove=new PacketMove(this);
        server.broadcast(packetMove, w, this);
        ArrayList<ConnectionToClient> last=server.getLastBroadcast();
        for (ConnectionToClient client : last) {
            this.sendPacket(new PacketMove(client));
        }
    }
}
