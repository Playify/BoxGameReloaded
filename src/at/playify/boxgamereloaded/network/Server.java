package at.playify.boxgamereloaded.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import at.playify.boxgamereloaded.interfaces.Handler;
import at.playify.boxgamereloaded.level.EmptyServerLevel;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.level.compress.CompressionHandler;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.packet.Packet;

//Server wird auch für SinglePlayer benutzt
public class Server extends Thread{
    public CompressionHandler compresser=new CompressionHandler();
    private CopyOnWriteArrayList<ConnectionToClient> connected=new CopyOnWriteArrayList<>();
    private HashMap<String, ServerLevel> levelmap=new HashMap<>();
    private int pausemode= 2;
    public Handler handler;

    private ServerLevel empty;
    private final ThreadLocal<ArrayList<ConnectionToClient>> last = new ThreadLocal<>();
    private ServerSocket socket;

    public Server(Handler handler) {
        this.handler=handler;
    }

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

    @SuppressWarnings("unused")
    public void setPausemode(int pausemode) {
        this.pausemode = pausemode;
        System.out.println("Pause Mode changed to "+pausemode);
    }

    public void run() {
        try {
            socket=new ServerSocket(45565);
            //noinspection InfiniteLoopStatement
            while (true) {
                connected.add(new ConnectionToClient(socket.accept(), this));
            }
        } catch (SocketException e) {
            System.out.println("Server closed");
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerLevel getLevel(String world) {
        if (levelmap.containsKey(world)) {
            return levelmap.get(world);
        }else {
            try {
                boolean paint=world.startsWith("paint");
                JSONObject levels=world.startsWith("paint") ?handler.read("paint"):handler.assetJson( "leveldata");
                if (paint){
                    boolean found=false;
                    JSONObject n=new JSONObject();
                    Iterator<String> keys=levels.keys();
                    while (keys.hasNext()) {
                        String s=keys.next();
                        if (s.startsWith("paint_")) {
                            found=true;
                            n.put("paint"+s.substring(6),levels.get(s));
                        }else n.put(s,levels.get(s));
                    }
                    if (found){
                        handler.write("paint",n);
                        levels=n;
                    }
                }
                if (levels.has(world)) {
                    JSONObject object=levels.getJSONObject(world);
                    if (object.has("data")) {
                        ServerLevel level=new ServerLevel(this);
                        level.loadWorldString(object.getString("data"));
                        levelmap.put(world, level);
                        return level;
                    }
                    System.err.println("Level file is incorrect");
                    return empty==null ? empty=new EmptyServerLevel(this) : empty;
                } else {
                    return empty==null ? empty=new EmptyServerLevel(this) : empty;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return empty==null ? empty=new EmptyServerLevel(this) : empty;
            }
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

    public ConnectionToClient getByName(String name) {
        for (ConnectionToClient connectionToClient : connected) {
            if (name.equals(connectionToClient.name)) {
                return connectionToClient;
            }
        }
        return null;
    }

    public ArrayList<ConnectionToClient> getLastBroadcast() {
        return last.get();
    }

    public void disconnect(ConnectionToClient connectionToClient) {
        connected.remove(connectionToClient);
    }

    public void connect(ConnectionToClient connectionToClient) {
        connected.add(connectionToClient);
    }

    public void getByWorld(String world, ArrayList<ConnectionToClient> list) {
        list.clear();
        for (ConnectionToClient connectionToClient : connected) {
            if (world.equals(connectionToClient.world)) {
                list.add(connectionToClient);
            }
        }
    }

    public void close() {
        for (ConnectionToClient connectionToClient : connected) {
            if (connectionToClient.socket!=null) {
                connectionToClient.close();
            }
        }
        connected.clear();
        try {
            if (socket!=null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getLevels(String name) {
        ArrayList<String> list=new ArrayList<>();
        try {
            JSONObject levels=handler.assetJson("levels");
            if (levels.has(name)) {
                JSONArray arr=levels.getJSONArray(name);
                for(int i=0; i<arr.length(); i++) {
                    list.add(arr.getString(i));
                }
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return list;
        }
    }

    public ArrayList<String> getStages() {
        ArrayList<String> list=new ArrayList<>();
        try {
            JSONObject levels=handler.assetJson("levels");
            if (levels.has("list")) {
                JSONArray arr=levels.getJSONArray("list");
                for(int i=0; i<arr.length(); i++) {
                    list.add(arr.getString(i));
                }
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
            return list;
        }
    }

    public boolean isLevel(String s) {
        try {
            s+="=";
            JSONObject levels=handler.assetJson("levels");
            Iterator<String> keys=levels.keys();
            while (keys.hasNext()) {
                String key=keys.next();
                if (!key.equals("list")) {
                    JSONArray arr=levels.getJSONArray(key);
                    int length=arr.length();
                    for(int i=0; i<length; i++) {
                        if (arr.getString(i).startsWith(s)) {
                            return true;
                        }
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
}
