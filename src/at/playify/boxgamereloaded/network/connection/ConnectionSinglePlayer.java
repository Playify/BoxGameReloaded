package at.playify.boxgamereloaded.network.connection;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.Server;

import java.io.*;
import java.net.Socket;

//Verbindung zum SinglePlayer Server [WIP]
public class ConnectionSinglePlayer extends ConnectionToServer implements Closeable {

    private final Server server;
    private Socket socket;

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
    }

    public boolean isLanWorld() {
        return server.getState() != Thread.State.NEW;
    }

    public void openLanWorld() {
        if (!isLanWorld()) {
            server.start();
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    public void close() {
    }


}
