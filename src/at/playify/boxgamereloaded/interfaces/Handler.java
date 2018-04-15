package at.playify.boxgamereloaded.interfaces;

//Benutzt um dem Spiel Zugriff auf eine Config Datei zu geben und sonstige Betriebssystemabhängige Aktionen.
public interface Handler {
    //Return true if the game should stop receiving keyevents
    boolean isKeyboardVisible();

    //toggle keyboard on/off
    void setKeyboardVisible(boolean b);

    String getClipboardString();

    void setClipboardString(String s);

}
