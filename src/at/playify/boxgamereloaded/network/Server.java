package at.playify.boxgamereloaded.network;

import at.playify.boxgamereloaded.level.EmptyServerLevel;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//Server wird später auch für singleplayer benutzt
public class Server extends Thread{
    private CopyOnWriteArrayList<ConnectionToClient> connected=new CopyOnWriteArrayList<>();
    private HashMap<String, ServerLevel> levelmap=new HashMap<>();
    private int pausemode= 2;

    private ServerLevel empty;
    private final ThreadLocal<ArrayList<ConnectionToClient>> last = new ThreadLocal<>();

    public int getPausemode() {
        return pausemode;
    }

    public void setPausemode(int pausemode) {
        this.pausemode = pausemode;
        System.out.println("Pause Mode changed to "+pausemode);
    }

    public void run() {

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(45565);
            //noinspection InfiniteLoopStatement
            while (true) {
                connected.add(new ConnectionToClient(serverSocket.accept(),this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerLevel getLevel(String world) {
        if (levelmap.containsKey(world)) {
            return levelmap.get(world);
        }else {
            File file = new File("levels", world + ".txt");
            if (file.exists()) {
                ServerLevel level = new ServerLevel();
                level.loadWorldString(file(file));
                levelmap.put(world, level);
                return level;
            }else{
                return empty==null?empty=new EmptyServerLevel():empty;
            }
        }
    }

    private String file(File file) {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(file.toURI()) ) );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return content;
    }

    public void broadcast(Packet packet) {
        if (last.get()==null) {
            last.set(new ArrayList<>());
        }
        last.get().clear();
        for (ConnectionToClient connectionToClient : connected) {
            connectionToClient.sendPacket(packet);
                last.get().add(connectionToClient);
        }
    }
    @SuppressWarnings("WeakerAccess")
    public void broadcastRaw(String s) {
        if (last.get()==null) {
            last.set(new ArrayList<>());
        }
        last.get().clear();
        for (ConnectionToClient connectionToClient : connected) {
            connectionToClient.sendRaw(s);
                last.get().add(connectionToClient);
        }
    }

    public void broadcast(Packet packet, ConnectionToClient except) {
        if (last.get()==null) {
            last.set(new ArrayList<>());
        }
        last.get().clear();
        for (ConnectionToClient connectionToClient : connected) {
            if (connectionToClient!=except) {
                connectionToClient.sendPacket(packet);
                last.get().add(connectionToClient);
            }
        }
    }

    public void broadcast(Packet packet, String world, ConnectionToClient except) {
        if (last.get()==null) {
            last.set(new ArrayList<>());
        }
        last.get().clear();
        for (ConnectionToClient connectionToClient : connected) {
            if (connectionToClient!=except&&world.equals(connectionToClient.world)){
                connectionToClient.sendPacket(packet);
                last.get().add(connectionToClient);
            }
        }
    }

    public void checkConnected() {
        for (ConnectionToClient next : connected) {
            if (next.isClosed()) {
                next.close();
            } else {
                last.get().add(next);
            }
        }
    }

    public ArrayList<ConnectionToClient> getLastBroadcast() {
        return last.get();
    }

    public void disconnect(ConnectionToClient connectionToClient) {
        connected.remove(connectionToClient);
    }
}
