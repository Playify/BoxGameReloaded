package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.packet.*;
import at.playify.boxgamereloaded.util.Action;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

//Verbindung zum Client
public class ConnectionToClient extends Thread implements Closeable {
    protected final Server server;
    public String skin;
    private Socket socket;
    public String version;
    public int switchState;
    public RectBound collider=new RectBound();
    private boolean closed;
    public String world="NULL";
    public String name;
    public RectBound bound=new RectBound(.1f, 0, .8f, .8f);
    public boolean paused;
    private Input input;
    private Output output;
    private final Object packetLock=new Object();
    private boolean errored;

    public ConnectionToClient(Socket socket, Server server){
        this.server=server;
        try {
            this.socket=socket;
            input=new Input(socket.getInputStream());
            output=new Output(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            closed=true;
            return;
        }
        setName("ConnectionToClient");
        start();
        System.out.println("Opened Connection to "+socket.getInetAddress());
    }

    public ConnectionToClient(InputStream is, OutputStream os, Server server){
        input=new Input(is);
        output=new Output(os);
        this.server=server;
        setName("ConnectionToSinglePlayer");
        start();
        System.out.println("Opened Connection to SinglePlayer");
    }

    public void sendPacket(Packet packet){
        if (output!=null) {
            try {
                synchronized (packetLock) {
                    output.writeString(packet.getClass().getSimpleName().substring(6));
                    packet.send(output, server, this);
                    output.flush();
                }
                packet.onSend(server, this);
            } catch (EOFException e) {
                if (!errored) {
                    errored=true;
                    server.logger.error("Connection lost to Player \""+name+"\" Reason: EOF("+e.getMessage()+")");
                }
                close();
            } catch (SocketException e) {
                if (!errored) {
                    errored=true;
                    server.logger.error("Connection lost to Player \""+name+"\" Reason: "+e.getMessage());
                }
                close();
            } catch (Exception e) {
                if (!errored) {
                    errored=true;
                    server.logger.error("Error in ConnectionToClient");
                }
                e.printStackTrace();
                close();
            }
        }
    }

    public boolean isClosed(){
        return closed||(socket!=null&&socket.isClosed());
    }

    public void close(String reason){
        sendPacket(new PacketKick(reason));
        close();
    }


    @Override
    public void close(){
        try {
            if (socket!=null) {
                socket.close();
                socket=null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            server.broadcast(new PacketResetPlayersInWorld(name), world, this);
            if (paused) {
                paused=false;
                server.broadcast(new PacketServerSettings(server.getPausemode()), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.disconnect(this);
    }

    @Override
    public void run(){
        try {
            while (true) {
                String packetName=input.readString();
                try {
                    try {
                        Object packet=Class.forName(Packet.class.getName()+packetName).newInstance();
                        if (packet instanceof Packet) {
                            ((Packet) packet).receive(input, server, this);
                            ((Packet) packet).handle(server, this);
                        }
                    } catch (ClassNotFoundException cls) {
                        if (!errored) {
                            server.logger.error("Unknown Packet received: "+packetName);
                            errored=true;
                        }
                        close();
                    } catch (ClassCastException cls) {
                        if (!errored) {
                            server.logger.error("Wrong Packet received: "+packetName);
                            errored=true;
                        }
                        close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (EOFException e) {
            if (!errored) {
                errored=true;
                server.logger.error("Connection lost to Player \""+name+"\" Reason: EOF("+e.getMessage()+")");
            }
            close();
        } catch (SocketException e) {
            if (!errored) {
                errored=true;
                server.logger.error("Connection lost to Player \""+name+"\" Reason: "+e.getMessage());
            }
            close();
        } catch (Exception e) {
            if (!errored) {
                errored=true;
                server.logger.error("Error in ConnectionToClient");
            }
            e.printStackTrace();
            close();
        }
    }

    private ArrayList<ConnectionToClient> list=new ArrayList<>();

    public void setWorld(final String w){
        server.levels.getLevel(w, new Action<ServerLevel>() {
            @Override
            public void exec(ServerLevel level){
                server.broadcast(new PacketResetPlayersInWorld(name), world, ConnectionToClient.this);
                boolean b=false;
                if (!world.equals(w)) {
                    switchState=0;
                    b=true;
                    world=w;
                    sendPacket(new PacketSetWorld(w));
                    sendPacket(new PacketSpawn(level.spawnPoint));
                    bound.set(level.spawnPoint);
                }
                server.broadcast(new PacketMove(ConnectionToClient.this), world, ConnectionToClient.this);
                server.broadcast(new PacketSkin(name, skin), world, ConnectionToClient.this);
                sendPacket(new PacketLevelData(level.toWorldString()));
                sendPacket(new PacketResetPlayersInWorld());
                server.getByWorld(world, list);
                int switchState=0;
                for (int i=list.size()-1;i >= 0;i--) {
                    ConnectionToClient con=list.get(i);
                    if (con!=null) {
                        switchState|=con.switchState;
                    }
                }
                sendPacket(new PacketSwitch(switchState));
                for (int i=list.size()-1;i >= 0;i--) {
                    ConnectionToClient client=list.get(i);
                    if (client!=null) {
                        sendPacket(new PacketMove(client));
                        sendPacket(new PacketSkin(client.name, client.skin));
                    }
                }
                if (b) {
                    sendPacket(new PacketMove(level.spawnPoint, name));
                }
            }
        });
    }
}
