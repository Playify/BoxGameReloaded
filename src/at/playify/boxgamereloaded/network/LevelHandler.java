package at.playify.boxgamereloaded.network;

import at.playify.boxgamereloaded.interfaces.Handler;
import at.playify.boxgamereloaded.level.EmptyServerLevel;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.util.Action;
import at.playify.boxgamereloaded.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelHandler implements Comparator<String> {
    private HashMap<String, ServerLevel> levelmap=new HashMap<>();
    private ServerLevel empty;
    private Handler handler;
    private AtomicLong lng=new AtomicLong();
    private Server server;
    private boolean dirty;
    private JSONObject down;

    public LevelHandler(Server server) {
        this.server=server;
        this.handler=server.handler;
        empty=new EmptyServerLevel(server);
    }

    public void getNext(final String world, final Action<String> action) {
        new Executor() {
            @Override
            public void run() {
                try {
                    JSONObject json=get(world);
                    if (json.has(world)) {
                        JSONObject wrld=json.getJSONObject(world);
                        if (wrld.has("next")) {
                            action.exec(wrld.getString("next"));
                            return;
                        }
                    }
                    Pattern compile=Pattern.compile("([a-zA-Z]*)([0-9]+)");
                    Matcher matcher=compile.matcher(world);
                    if (matcher.matches()) {
                        String pre=matcher.group(1);
                        String w=pre+(Integer.parseInt(matcher.group(2))+1);
                        if (isLevel(w)) {
                            action.exec(w);
                            return;
                        }
                        w=pre+"0";
                        if (isLevel(w)) {
                            action.exec(w);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                action.exec(world);
            }
        };
    }

    public void getLevel(final String world, final Action<ServerLevel> action) {
        new Executor() {
            @Override
            public void run() {
                try {
                    if (levelmap.containsKey(world)) {//already loaded
                        ServerLevel lvl=levelmap.get(world);
                        ArrayList<ConnectionToClient> lst=new ArrayList<>();
                        server.getByWorld(world,lst);
                        if (world.startsWith("down")&&lst.size()==0) {
                            JSONObject json=get("down");
                            if (json.has(world)) {
                                String data=json.getJSONObject(world).getString("data");
                                if (!lvl.toWorldString().equals(data)) {
                                    lvl.loadWorldString(data);
                                }
                            }
                        }
                        action.exec(lvl);
                        return;
                    }
                    JSONObject json=get(world);
                    if (json.has(world)) {
                        JSONObject wrld=json.getJSONObject(world);
                        ServerLevel level=new ServerLevel(server);
                        level.loadWorldString(wrld.getString("data"));
                        levelmap.put(world, level);
                        action.exec(level);
                    } else {
                        action.exec(empty);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    action.exec(empty);
                }
            }
        };
    }

    public void getLevels(final String stage, final Action<ArrayList<String>> action) {
        new Executor() {
            @Override
            public void run() {
                JSONObject json=get(stage);
                Iterator<String> iterator=json.keys();
                ArrayList<String> list=new ArrayList<>();
                while (iterator.hasNext()) {
                    String next=iterator.next();
                    if (next.startsWith(stage)) {
                        try {
                            JSONObject wrld=json.getJSONObject(next);
                            String name=wrld.has("name") ? wrld.getString("name") : name(next);
                            String by=wrld.has("by") ? wrld.getString("by") : "";
                            list.add(next+"="+name+"="+by);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Collections.sort(list, LevelHandler.this);
                if (stage.equals("paint")) {
                    int size;
                    try {
                        size=handler.assetJson("stages").getInt("paintSize");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        size=0;
                    }
                    for(int i=0; i<size; i++) {
                        boolean found=false;
                        String name="paint"+i+"=";
                        for(int j=list.size()-1; j >= 0; j--) {
                            if (list.get(j).startsWith(name)) {
                                found=true;
                                break;
                            }
                        }
                        if (!found) {
                            list.add(name+"Paint "+i+"=");
                        }
                    }
                }
                Collections.sort(list, LevelHandler.this);

                action.exec(list);
            }
        };
    }

    public void markDirty() {
        dirty=true;
    }

    public void getStages(final Action<ArrayList<String>> action) {
        new Executor() {
            @Override
            public void run() {
                try {
                    JSONObject json=handler.assetJson("stages");
                    JSONArray array=json.getJSONArray("list");
                    ArrayList<String> list=new ArrayList<>();
                    for(int i=0; i<array.length(); i++) {
                        list.add(array.getString(i));
                    }
                    action.exec(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                    action.exec(new ArrayList<String>());
                }
            }
        };
    }

    public void upload(final String player,final String version, final String lvl, final String levelstr,final Action<String> action) {
        new Executor() {

            @Override
            public void run() {
                //TODO username/password protection
                try (Scanner scanner=new Scanner(new URL("http://playify.site90.net/bxgm/up?user="+player+"&version="+version+"&lvl="+lvl+"&data="+levelstr).openStream(), "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    String txt=scanner.hasNext() ? scanner.next() : "";
                    action.exec(txt);
                } catch (SocketException e) {
                    server.logger.error("Error connecting to Webserver (Upload)");
                    action.exec("$Error connecting to Webserver!");
                } catch (IOException e) {
                    server.logger.error("Error uploading Levels");
                    e.printStackTrace();
                    action.exec("$Error");
                }
            }
        };
    }

    @Override
    public int compare(String s1, String s2) {
        s1=s1.substring(0, s1.indexOf('='));
        s2=s2.substring(0, s2.indexOf('='));
        boolean down=true;
        for(char c : s1.toCharArray()) {
            if (Character.isLetter(c)!=down) {
                if (down) {
                    down=false;
                } else {
                    return s1.compareTo(s2);
                }
            }
        }
        if (down) {
            return s1.compareTo(s2);
        }
        int ret;
        if ((ret=Boolean.compare(s1.startsWith("down"), s2.startsWith("down")))!=0) return ret;
        if ((ret=Boolean.compare(s1.startsWith("paint"), s2.startsWith("paint")))!=0) return ret;
        if ((ret=Boolean.compare(s1.startsWith("lvl"), s2.startsWith("lvl")))!=0) return ret;
        int i, l=Math.min(s1.length(), s2.length());
        for(i=0; i<l; i++) {
            char c1=s1.charAt(i);
            char c2=s2.charAt(i);
            if (Character.isDigit(c1)&&Character.isDigit(c2)) {
                break;
            } else {
                if ((ret=Character.compare(c1, c2))!=0) return ret;
            }
        }
        return Integer.compare(Utils.parseInt(s1.substring(i), -1), Utils.parseInt(s2.substring(i), -1));
    }

    private JSONObject get(String world) {
        if (world.startsWith("paint")) return handler.read("paint");
        else if (world.startsWith("lvl")) return handler.assetJson("levels");
        else if (world.startsWith("down")) {
            if (down==null) dirty=true;
            if (!dirty) return down;
            try {
                try (Scanner scanner=new Scanner(new URL("http://playify.site90.net/bxgm/down").openStream(), "UTF-8")) {
                    scanner.useDelimiter("\\A");
                    String txt=scanner.hasNext() ? scanner.next() : "";
                    down=new JSONObject(txt);
                    dirty=false;
                    return down;
                } catch (SocketException|UnknownHostException e) {
                    server.logger.error("Error connecting to Webserver (Download)");
                    JSONObject json=new JSONObject();
                    JSONObject wrld=new JSONObject();
                    wrld.put("name", "$No Network");
                    json.put("down", wrld);
                    down=json;
                    dirty=false;
                    return down;
                } catch (IOException e) {
                    e.printStackTrace();
                    down=new JSONObject();
                    dirty=false;
                    return down;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                down=new JSONObject();
                dirty=false;
                return down;
            }
        } else return handler.assetJson("levels");
    }

    private String name(String world) {
        if (world.startsWith("lvl")) return "Level "+world.substring(3);
        if (world.startsWith("paint")) return "Paint "+world.substring(5);
        if (world.startsWith("down")) return "Download "+world.substring(4);
        return world;
    }

    private boolean isLevel(String s) {
        try {
            if (s.startsWith("paint")) {
                int i=Utils.parseInt(s.substring(5), -1);
                return i >= 0&&i<handler.assetJson("stages").getInt("paintSize");
            }
            JSONObject json=get(s);
            return json.has(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private abstract class Executor extends Thread {
        private Executor() {
            setName("LevelHandler Thread #"+lng.getAndIncrement());
            start();
        }

        public abstract void run();
    }
}
