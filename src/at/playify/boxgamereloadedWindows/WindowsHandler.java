package at.playify.boxgamereloadedWindows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Handler;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;

public class WindowsHandler extends Handler {
    BoxGameReloaded game;
    WindowsDrawer d;
    private boolean keybd;
    private Frame frame;
    private boolean external=true;
    private ServerSocket server;
    private Thread thr;

    @Override
    public boolean isKeyboardVisible() {
        return keybd;
    }

    @Override
    public void setKeyboardVisible(boolean b) {
        keybd=b;
        if (external) {
            if (server==null||server.isClosed()) {
                try {
                    server=new ServerSocket(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (server!=null) {
                if (thr!=null) {
                    thr.interrupt();
                }
                thr=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket accept=server.accept();
                            thr=null;
                            BufferedReader br=new BufferedReader(new InputStreamReader(accept.getInputStream()));
                            String s;
                            while ((s=br.readLine())!=null) {
                                if (!s.equals("\b\b")) {
                                    game.runcmd(s);
                                }
                            }
                        } catch (SocketException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thr.start();
                int port=server.getLocalPort();
                try {
                    File file=new File(WindowsHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/c",
                            "title BoxGameReloaded Console&&java.exe -cp \""+file+"\" "+MainConsole.class.getName()+" "+port});
                } catch (IOException|URISyntaxException e) {
                    e.printStackTrace();
                }


            } else {
                System.err.println("Error initializing Server");
                external=false;
                setKeyboardVisible(true);
                external=true;
            }
        } else {
            if (b) {
                if (frame!=null) return;
            } else {
                if (frame!=null) {
                    frame.setVisible(false);
                    frame=null;
                    return;
                }
            }
            frame=new Frame("BoxGame CMD");
            TextField txt=new TextField();
            frame.add(txt);
            frame.setAlwaysOnTop(true);
            int h=60;
            frame.setSize(new Dimension(200, h));
            frame.setPreferredSize(new Dimension(200, h));
            frame.setMinimumSize(new Dimension(200, h));
            frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, h+1));
            frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {
                    frame.setVisible(false);
                    frame=null;
                }

                @Override
                public void windowClosed(WindowEvent e) {

                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
            frame.setLocation(Display.getX()+Display.getWidth()/2-100, Display.getY()+Display.getHeight()/2-h/2);
            frame.setVisible(true);
        }
    }

    @Override
    public File baseDir(String filename) {
        return Main.base;
    }

}
