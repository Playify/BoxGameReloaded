package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.stream.PipedInputStream;
import at.playify.boxgamereloaded.network.connection.stream.PipedOutputStream;

import java.io.*;


//Verbindung zum SinglePlayer Server [WIP]
@SuppressWarnings("WeakerAccess")
public class ConnectionSinglePlayer extends ConnectionToServer implements Closeable {

    private final Server server;

    public ConnectionSinglePlayer(BoxGameReloaded game) {
        super(game);
        server=new Server(game.handler);
        PipedInputStream toClient = new PipedInputStream();
        PipedOutputStream fromClient = new PipedOutputStream();
        this.in = new BufferedReader(new InputStreamReader(toClient));
        this.out = new PrintStream(fromClient);
        try {
            server.connect(new ConnectionToClient(new PipedInputStream(fromClient), new PipedOutputStream(toClient), server));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(this);
        thread.setName("ConnectionToSinglePlayerServer");
        thread.start();
        initPackets();
    }


    public boolean isLanWorld() {
        return server.running();
    }

    public void openLanWorld() {
        if (!server.running()) {
            server.start();
        }
    }
    public void closeLanWorld() {
        if (server.running()) {
            server.stop();
        }
    }

    @Override
    public boolean isClosed() {
        return closed||out.checkError()||server.isClosed();
    }

    public void close() {
        server.close();
        closed=true;
    }

    @Override
    public String getIp() {
        return "SinglePlayer";
    }
}
