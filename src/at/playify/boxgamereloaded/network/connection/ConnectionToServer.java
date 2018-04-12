package at.playify.boxgamereloaded.network.connection;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.network.packet.Packet;
import at.playify.boxgamereloaded.network.packet.PacketSetWorld;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.util.bound.RectBound;

//Verbindung zum Server [WIP]
public class ConnectionToServer implements Closeable {
    public Player[] players=new Player[0];
    public boolean connected;
    public String servername;
    public boolean pause;
    public boolean userpause;
    protected BoxGameReloaded game;
    private Socket socket;
    private boolean closed;
    private BufferedReader in;
    private PrintStream out;
    private Queue<Packet> q=new LinkedList<>();
    public RectBound serverbound=new RectBound();
    public ConnectionToServer() {}

    public ConnectionToServer(final BoxGameReloaded game, String ip) {
        this.game=game;
        try {
            socket=new Socket(InetAddress.getByName(ip), 45565);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            Game.report(e);
            closed=true;
            return;
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s;
                    while ((s=in.readLine())!=null) {
                        try {
                            int i=s.indexOf(':');
                            String packetName=i==-1 ? s : s.substring(0, i);
                            try {
                                Class<? extends Packet> cls=(Class<? extends Packet>) Class.forName(Packet.class.getName()+packetName);
                                Packet packet=cls.newInstance();//TODO reuse objects
                                packet.loadFromString(i==-1 ? "" : s.substring(i+1), game);
                                packet.handle(game, ConnectionToServer.this);
                            } catch (ClassNotFoundException cls) {
                                System.err.println("Unknown Packet received: "+packetName);
                            } catch (ClassCastException cls) {
                                System.err.println("Wrong Packet received: "+packetName);
                            }
                        }catch (Exception e){
                            Game.report(e);
                        }
                    }
                } catch (SocketException e) {
                    System.err.println("Connection to Server Closed: "+e.getMessage());
                    close();
                } catch (Exception e) {
                    System.err.println("Error in ConnectionToServer");
                    Game.report(e);
                }
            }
        });
        thread.setName("ConnectionToClient");
        thread.start();
    }

    public void leaveWorld() {
        sendPacketSoon(new PacketSetWorld("NONE"));
    }

    public void sendPacket(Packet packet) {
        out.println(packet.getClass().getSimpleName().substring(6)+":"+packet.convertToString(game));
        out.flush();
    }

    public void sendPacketSoon(Packet packet) {
        q.add(packet);
    }

    public boolean isClosed() {
        return closed|socket.isClosed();
    }

    public void handleSoon() {
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
    public void close() {
        try {
            if (socket!=null) {
                socket.close();
            }
        } catch (IOException e) {
            Game.report(e);
        }
    }

    public boolean isPaused(boolean partly) {
        if (userpause) {
            return game.paused||pause||(!partly&&game.backgroundState!=0);
        } else {
            if (pause) {
                game.paused=true;
                return true;
            } else {
                return false;
            }
        }
    }
}