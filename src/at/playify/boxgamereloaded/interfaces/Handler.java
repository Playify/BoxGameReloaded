package at.playify.boxgamereloaded.interfaces;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Scanner;

//Benutzt um dem Spiel Zugriff auf eine Config Datei zu geben und sonstige Betriebssystemabhängige Aktionen.
public abstract class Handler {
    private final HashMap<String,JSONObject> map=new HashMap<>();
    private final HashMap<String,Long> time=new HashMap<>();

    //Return true if the game should stop receiving keyevents
    public abstract boolean isKeyboardVisible();

    //toggle keyboard on/off
    public abstract void setKeyboardVisible(boolean b);

    //Json aus datei lesen
    //priv wird privat gespeichert (Android) um sicher spielfortschritt speichern zu können
    public JSONObject read(String filename) {
        try {
            File file=new File(baseDir(filename), filename+".json");
            if (map.containsKey(filename)) {
                if (!file.exists()||file.lastModified()==time.get(filename)) {
                    return map.get(filename);
                }
            }
            try (Scanner inputStream=new Scanner(file)) {
                JSONTokener tokener=new JSONTokener(inputStream.useDelimiter("\\Z").next());
                JSONObject json=new JSONObject(tokener);
                map.put(filename, json);
                time.put(filename, file.lastModified());
                return json;
            }
        } catch (FileNotFoundException e) {
            return new JSONObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    //Json in Datei schreiben
    //priv wird privat gespeichert (Android) um sicher spielfortschritt speichern zu können
    public void write(String filename, JSONObject o) {
        File file=new File(baseDir(filename), filename+".json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (FileWriter fw=new FileWriter(file)) {
            fw.write(o.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
        time.put(filename, file.lastModified());
    }

    public abstract File baseDir(String filename);

}
