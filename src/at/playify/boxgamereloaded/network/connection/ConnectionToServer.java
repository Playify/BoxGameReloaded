package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.network.packet.*;
import at.playify.boxgamereloaded.player.PlayerMP;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

//Verbindung zum Server
public class ConnectionToServer implements Closeable, Runnable {
    public PlayerMP[] players=new PlayerMP[0];
    public boolean connected;
    public String servername;
    public boolean pause;
    public boolean userpause;
    public int pauseCount;
    public ArrayList<String> all;
    public boolean switch0;
    public boolean switch1;
    public int switchState;
    public boolean collide;
    protected BoxGameReloaded game;
    private Socket socket;
    protected Input input;
    protected Output output;
    boolean closed;
    public final RectBound serverbound=new RectBound(-1, -1, -1, -1);
    private final Queue<Packet> q=new LinkedList<>();
    public final Object playerLock=new Object();
    private final Object packetLock=new Object();
    private boolean errored;

    public ConnectionToServer(){
    }

    public ConnectionToServer(final BoxGameReloaded game, String ip){
        this.game=game;
        try {
            socket=new Socket(InetAddress.getByName(ip), 45565);
            input=new Input(socket.getInputStream());
            output=new Output(socket.getOutputStream());
        } catch (IOException e) {
            game.logger.error(e.getClass().getSimpleName()+":"+e.getMessage());
            closed=true;
            return;
        }
        Thread thread=new Thread(this);
        thread.setName("ConnectionToServer");
        thread.start();
        initPackets();
    }

    void initPackets(){
        sendPacket(new PacketHello());
        sendPacket(new PacketSetWorld(game.vars.world));
        if (game.gui.isMainMenuVisible()) {
            sendPacket(new PacketMainMenu());
        }
    }

    protected ConnectionToServer(BoxGameReloaded game){
        this.game=game;
    }

    public void leaveWorld(){
        sendPacketSoon(new PacketSetWorld("Lobby"));
    }


    public void sendPacket(Packet packet){
        if (output!=null&&!isClosed()) {
            try {
                synchronized (packetLock) {
                    output.writeString(packet.getClass().getSimpleName().substring(6));
                    packet.send(output, game, this);
                    output.flush();
                }
                packet.onSend(game, this);
            } catch (SocketException e) {
                if (!errored) {
                    errored=true;
                    String message=e.getMessage();
                    if (message.startsWith("Software caused connection abort: ")) {
                        message=message.substring("Software caused connection abort: ".length());
                    }
                    game.logger.error("Connection to Server Closed: "+message);
                }
                closed=true;
                close();
            } catch (Exception e) {
                if (!errored) {
                    errored=true;
                    game.logger.error("Error in "+getClass().getSimpleName());
                }
                closed=true;
                close();
            }
        }
    }

    public void sendPacketSoon(Packet packet){
        q.add(packet);
        game.pauseLock.unlock();
    }

    public boolean isClosed(){
        return closed||socket==null||socket.isClosed();
    }

    public void handleSoon(){
        while (true) {
            Packet poll=q.poll();
            if (poll==null) {
                return;
            } else {
                sendPacket(poll);
            }
        }
    }

    @Override
    public void close(){
        sendPacket(new PacketKick());
        closed=true;
        try {
            if (socket!=null) {
                socket.close();
            }
        } catch (IOException e) {
            Game.report(e);
        }
    }

    public boolean isPaused(boolean partly){
        if (game.gui.isMainMenuVisible()) return false;
        if (userpause) {
            return game.paused||(!partly&&game.gui.backgroundState()!=0);
        } else {
            if (pause) {
                game.paused=true;
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void run(){
        String packetName="";
        try {
            while (true) {
                packetName=input.readString();
                Object packet=Class.forName(Packet.class.getName()+packetName).newInstance();
                if (packet instanceof Packet) {
                    ((Packet) packet).receive(input, game, this);
                    ((Packet) packet).handle(game, this);
                }
            }
        } catch (ClassNotFoundException cls) {
            if (!errored) {
                errored=true;
                game.logger.error("Unknown Packet received: "+packetName);
            }
            close();
        } catch (ClassCastException cls) {
            if (!errored) {
                errored=true;
                game.logger.error("Wrong Packet received: "+packetName);
            }
            close();
        } catch (InstantiationException cls) {
            if (!errored) {
                errored=true;
                game.logger.error("Illegal Packet received: "+packetName);
            }
            close();
        } catch (SocketException e) {
            if (!errored) {
                errored=true;
                String message=e.getMessage();
                if (message.startsWith("Software caused connection abort: ")) {
                    message=message.substring("Software caused connection abort: ".length());
                }
                game.logger.error("Connection to Server Closed: "+message);
            }
            close();
        } catch (Exception e) {
            if (!errored) {
                errored=true;
                game.logger.error("Error in "+getClass().getSimpleName());
            }
            Game.report(e);
            close();
        }
    }

    public String getIp(){
        if (socket==null) {
            return "[Not Connected]";
        } else {
            InetAddress add=socket.getInetAddress();
            return add.getHostAddress();
        }
    }

}
