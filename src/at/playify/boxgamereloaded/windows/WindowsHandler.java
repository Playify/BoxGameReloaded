package at.playify.boxgamereloaded.windows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Handler;
import at.playify.boxgamereloaded.util.Action;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
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
                                    game.cmd.run(s);
                                }
                            }
                        } catch (SocketException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thr.setName("Command Executor");
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
                game.logger.error("Error initializing Server");
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

    @Override
    public InputStream asset(String s) {
        return getClass().getResourceAsStream("/assets/"+s);
    }

    @Override
    public void setClipboard(String s) {
        StringSelection selection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    @Override
    public String getClipboard() {
        try {
            return ((String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException e) {
            return "";
        }
    }

    @Override
    public boolean isScrolling() {
        return Mouse.isButtonDown(1);
    }

    @Override
    public void keybd(String title, boolean pw, String preEnteredText, final Action.Bool<String> action) {
        final Dialog dialog=new Dialog((Window) null, title);
        final TextField text=new TextField();
        text.setFont(new Font(null,Font.PLAIN,30));
        text.setText(preEnteredText);
        text.setCaretPosition(preEnteredText.length());
        if (pw) {
            text.setEchoChar('\u2022');
        }
        text.setColumns(40);
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (action.exec(text.getText())) {
                    dialog.setVisible(false);
                }
            }
        });

        dialog.add(text);
        dialog.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(false);
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
        dialog.setAlwaysOnTop(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public boolean buttons() {
        return false;
    }
}
