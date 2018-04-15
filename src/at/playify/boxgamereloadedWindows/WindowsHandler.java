package at.playify.boxgamereloadedWindows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Handler;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class WindowsHandler implements Handler{
    BoxGameReloaded game;
    WindowsDrawer d;
    private boolean keybd;
    private Frame frame;
    private JDialog dialog;

    @Override
    public boolean isKeyboardVisible() {
        return keybd;
    }

    @Override
    public void setKeyboardVisible(boolean b) {
        keybd = b;
        //TODO open Command Window
        if (b) {
            if (dialog != null) return;
        } else {
            if (dialog != null) {
                dialog.setVisible(false);
                dialog = null;
                return;
            }
        }
        Thread[] thread = new Thread[1];
        thread[0] = new Thread(new Runnable() {
            @Override
            public void run() {
                final JTextField cmd = new JTextField();
                cmd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        game.runcmd(cmd.getText());
                        if (dialog != null) {
                            dialog.setVisible(false);
                            dialog = null;
                        }
                    }
                });
                Object[] message = {"Enter Command", cmd};

                JOptionPane pane = new JOptionPane(message,
                        JOptionPane.PLAIN_MESSAGE,
                        JOptionPane.OK_CANCEL_OPTION);
                dialog = pane.createDialog(null, "BoxGameReloaded CMD");
                dialog.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {

                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        dialog = null;
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
                cmd.requestFocusInWindow();
                dialog.setVisible(true);
                if (dialog != null) {
                    game.runcmd(cmd.getText());
                    dialog = null;
                }
            }
        });
        thread[0].setName("CMD");
        thread[0].start();
    }

    @Override
    public String getClipboardString() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void setClipboardString(String s) {
        StringSelection ss = new StringSelection(s);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
    }

}
