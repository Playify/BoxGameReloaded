package at.playify.boxgamereloaded.interfaces;

import org.json.JSONObject;

//Benutzt um dem Spiel Zugriff auf eine Config Datei zu geben und sonstige Betriebssystemabhängige Aktionen.
public interface Handler {
    //Return true if the game should stop receiving keyevents
    boolean isKeyboardVisible();

    //toggle keyboard on/off
    void setKeyboardVisible(boolean b);

    String getClipboardString();

    void setClipboardString(String s);

    JSONObject read(String file);

    void write(String file, JSONObject o);

}
