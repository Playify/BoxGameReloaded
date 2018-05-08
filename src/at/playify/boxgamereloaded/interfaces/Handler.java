package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.util.Utils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
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
            JSONObject json=new JSONObject();
            map.put(filename, json);
            time.put(filename, 0L);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject json=new JSONObject();
            map.put(filename, json);
            time.put(filename, 0L);
            return json;
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
        map.put(filename,o);
        time.put(filename, file.lastModified());
    }

    public abstract File baseDir(String filename);

    public String ip() {
        InetAddress localhost=null;
        InetAddress network=null;
        try {
            Enumeration<NetworkInterface> net=NetworkInterface.getNetworkInterfaces();
            while (net.hasMoreElements()) {
                NetworkInterface networkInterface=net.nextElement();
                Enumeration<InetAddress> inetAddresses=networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress=inetAddresses.nextElement();
                    String hostAddress=inetAddress.getHostAddress();
                    if (hostAddress.startsWith("127.")) {
                        localhost=inetAddress;
                    } else if (hostAddress.startsWith("192.168.")||hostAddress.startsWith("10.")||hostAddress.startsWith("172.16")) {
                        network=inetAddress;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (network!=null) {
            return network.getHostAddress();
        }
        if (localhost!=null) {
            return localhost.getHostAddress();
        }
        return "127.0.0.1";
    }

    @SuppressWarnings("unused")
    public String version(){
        InputStream inputStream=asset("version.txt");
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            String s=result.toString().replace("\r","");
            return s.substring(0,s.indexOf('\n'));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public JSONObject assetJson(String s){
        try {
            JSONTokener tokener=new JSONTokener(Utils.inputStreamToString(asset(s+".json")));
            return new JSONObject(tokener);
        }catch (Exception e){
            System.err.println("Error reading Json:"+s);
            e.printStackTrace();
            return null;
        }
    }

    public abstract InputStream asset(String s);

    public abstract void setClipboard(String s);

    public abstract String getClipboard();
}
