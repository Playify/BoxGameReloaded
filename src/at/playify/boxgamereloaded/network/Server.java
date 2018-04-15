package at.playify.boxgamereloaded.network;

import at.playify.boxgamereloaded.level.EmptyServerLevel;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.packet.Packet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

//Server wird später auch für singleplayer benutzt
public class Server extends Thread{
    private CopyOnWriteArrayList<ConnectionToClient> connected=new CopyOnWriteArrayList<>();
    private HashMap<String, ServerLevel> levelmap=new HashMap<>();
    private int pausemode= 2;

    private ServerLevel empty;
    private final ThreadLocal<ArrayList<ConnectionToClient>> last = new ThreadLocal<>();

    public int getPausemode() {
        if ((pausemode & 2) != 0) {
            for (ConnectionToClient connectionToClient : connected) {
                if (connectionToClient.paused) {
                    return 3;
                }
            }
        }
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
        StringBuilder fileContents = new StringBuilder((int) file.length());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append('\n');
            }
            return fileContents.toString();
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    public void broadcast(Packet packet) {
        if (last.get()==null) {
            last.set(new ArrayList<ConnectionToClient>());
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
            last.set(new ArrayList<ConnectionToClient>());
        }
        last.get().clear();
        for (ConnectionToClient connectionToClient : connected) {
            connectionToClient.sendRaw(s);
                last.get().add(connectionToClient);
        }
    }

    public void broadcast(Packet packet, ConnectionToClient except) {
        if (last.get()==null) {
            last.set(new ArrayList<ConnectionToClient>());
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
            last.set(new ArrayList<ConnectionToClient>());
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
        if (last.get() == null) {
            last.set(new ArrayList<ConnectionToClient>());
        }
        while (true) {
            boolean done = true;
            last.get().clear();
            for (ConnectionToClient next : connected) {
                if (next.isClosed()) {
                    next.close();
                    done = false;
                    break;
                } else {
                    last.get().add(next);
                }
            }
            if (done) {
                return;
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
