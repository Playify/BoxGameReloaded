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
    protected final Server server;
    public String skin;
    public Socket socket;
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

    public ConnectionToClient(InputStream is,OutputStream os,Server server) {
        in=new BufferedReader(new InputStreamReader(is));
        out=new PrintStream(os);
        this.server=server;
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

    public void close(String reason){
        sendPacket(new PacketKick(reason));
        close();
    }


    @Override
    public void close() {
        try {
            if (socket!=null) {
                socket.close();
                socket=null;
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

    private ArrayList<ConnectionToClient> list=new ArrayList<>();
    public void setWorld(String w) {
        server.broadcast(new PacketResetPlayersInWorld(this.name), world, this);
        ServerLevel level=server.getLevel(w);
        boolean b=false;
        if (!this.world.equals(w)) {
            b=true;
            this.world=w;
            this.sendPacket(new PacketSetWorld(w));
            this.sendPacket(new PacketSpawn(level.spawnPoint));
            bound.set(level.spawnPoint);
        }
        server.broadcast(new PacketMove(this), world, this);
        server.broadcast(new PacketSkin(name, skin), world, this);
        this.sendPacket(new PacketLevelData(level.toWorldString()));
        this.sendPacket(new PacketResetPlayersInWorld());
        server.getByWorld(world, list);
        list.remove(this);
        for (int i=list.size()-1;i >= 0;i--) {
            ConnectionToClient client=list.get(i);
            System.out.println(bound+" "+client.bound);
            this.sendPacket(new PacketMove(client));
            this.sendPacket(new PacketSkin(client.name, client.skin));
        }
        if (b) {
            this.sendPacket(new PacketMove(level.spawnPoint, name));
        }
    }
}
