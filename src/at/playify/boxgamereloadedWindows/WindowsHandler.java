package at.playify.boxgamereloadedWindows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Handler;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

class WindowsHandler implements Handler{
    BoxGameReloaded game;
    WindowsDrawer d;
    private boolean keybd;

    @Override
    public boolean isKeyboardVisible() {
        return keybd;
    }

    @Override
    public void setKeyboardVisible(boolean b) {
        keybd = b;
        //TODO open Command Window
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
