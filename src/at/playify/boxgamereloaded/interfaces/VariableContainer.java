package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.block.NoCollideable;
import at.playify.boxgamereloaded.util.bound.RectBound;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class VariableContainer {
    public final Loader loader=new Loader();
    private final BoxGameReloaded game;
    public Checkpoint check=new Checkpoint();
    @ConfigValue
    public Debug debug=new Debug();
    @ConfigValue
    public float display_size=10;
    @ConfigValue
    public float zoom_level=1.3f;
    @ConfigValue
    public boolean instant_zoom;
    @ConfigValue
    public boolean cubic=true;
    @ConfigValue
    public boolean cubic_check=true;
    @ConfigValue
    public boolean paintPoints;
    @ConfigValue
    public int deaths=0;
    @ConfigValue
    public ArrayList<String> finishedLevels=new ArrayList<>();
    public boolean tickOnDraw;
    public float tickrate=60;
    public int jumps=2;
    public boolean inverted_gravity;
    public boolean geometry_dash;
    public boolean fly;
    public boolean noclip;
    public boolean god;
    public String playerID;
    public boolean nameTags=true;
    public String world="Lobby";
    public boolean[] keys=new boolean[8];
    @ConfigValue
    public ConfigVariable skin=new ConfigVariable() {

        @Override
        public String get() {
            return game.player.skin();
        }

        @Override
        public void set(String s) {
            game.player.skin(s);
        }
    };
    @ConfigValue
    public String lastWorld;
    @ConfigValue
    public String stage;
    @ConfigValue
    public String lastversion;
    @ConfigValue
    public ArrayList<String> eggs=new ArrayList<>(Arrays.asList("cube"));
    @ConfigValue
    public boolean blockdata;

    public VariableContainer(Game game) {
        this.game=(BoxGameReloaded) game;
        String s=game.handler.getClass().getSimpleName();
        playerID=s.substring(0, s.length()-7)+"User@"+Long.toHexString(System.currentTimeMillis()^(System.nanoTime()<<16));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                VariableContainer.this.loader.save();
            }
        });
    }

    public class Debug {
        public boolean viewback;
        public boolean drawback;//Rückseite von Würfeln zeichnen
        @ConfigValue
        public boolean console;
        @ConfigValue
        public boolean paint;
        @ConfigValue
        public boolean text;
    }

    public class Checkpoint {
        private int jumps;
        private boolean geometry_dash;
        private boolean inverted_gravity;
        public RectBound bound=new RectBound();
        public boolean[] keys=new boolean[0];

        public void check() {
            check(game.player.bound);
        }

        public void die() {
            VariableContainer vars=VariableContainer.this;
            vars.geometry_dash=geometry_dash;
            vars.inverted_gravity=inverted_gravity;
            game.player.jumps=jumps;
            game.player.motionX=0;
            game.player.motionY=0;
            game.player.bound.set(bound);
            if (keys.length!=vars.keys.length) keys=new boolean[keys.length];
            System.arraycopy(keys, 0, vars.keys, 0, keys.length);
            for (Block block : game.blocks.list) {
                if (block instanceof NoCollideable) {
                    ((NoCollideable) block).onNoCollide(game.player, game.level);
                }
            }
        }

        public void check(RectBound spawnPoint) {
            VariableContainer vars=VariableContainer.this;
            geometry_dash=vars.geometry_dash;
            inverted_gravity=vars.inverted_gravity;
            jumps=game.player.jumps;
            bound.set(spawnPoint);
            if (keys.length!=vars.keys.length) keys=new boolean[vars.keys.length];
            System.arraycopy(vars.keys, 0, keys, 0, keys.length);
        }

        public void move(int x, int y) {
            bound.move(x, y);
        }
        public void shift(int x, int y,int sizeX,int sizeY) {
            bound.shift(x, y,sizeX,sizeY);
        }
    }

    public class Loader {
        private JSONObject json;

        public void load() {
            json=game.handler.read("config");
            load(VariableContainer.this, json);
            save(VariableContainer.this, json);
            game.handler.write("config", json);
        }

        private void load(Object o, JSONObject json) {
            for (Field field : o.getClass().getFields()) {
                if (field.getAnnotation(ConfigValue.class)!=null) {
                    Class<?> type=field.getType();
                    String name=field.getName();
                    try {
                        if (json.has(name)) {
                            if (type.equals(String.class)) field.set(o, json.getString(name));
                            else if (type.equals(int.class)) field.setInt(o, json.getInt(name));
                            else if (type.equals(byte.class)) field.setByte(o, (byte) json.getInt(name));
                            else if (type.equals(long.class)) field.setLong(o, json.getLong(name));
                            else if (type.equals(short.class)) field.setShort(o, (short) json.getInt(name));
                            else if (type.equals(boolean.class)) field.setBoolean(o, json.getBoolean(name));
                            else if (type.equals(char.class)) field.setChar(o, json.getString(name).charAt(0));
                            else if (type.equals(float.class)) field.setFloat(o, (float) json.getDouble(name));
                            else if (type.equals(double.class)) field.setDouble(o, json.getDouble(name));
                            else if (ConfigVariable.class.isAssignableFrom(type))
                                ((ConfigVariable) field.get(o)).set(json.getString(name));
                            else if (type.equals(ArrayList.class)) {
                                JSONArray arr=json.getJSONArray(name);
                                ArrayList<String> lst=new ArrayList<>();
                                for (int i=0;i<arr.length();i++) {
                                    lst.add(arr.getString(i));
                                }
                                field.set(o, lst);
                            } else {
                                load(field.get(o), json.getJSONObject(name));
                            }
                        }
                    } catch (JSONException|IllegalAccessException e) {
                        game.logger.error(e.getClass().getSimpleName()+" trying to read Config var:"+field.getName()+" in json:"+json);
                    }
                }
            }
        }

        public void save() {
            json=game.handler.read("config");
            save(VariableContainer.this, json);
            game.handler.write("config", json);
        }

        private void save(Object o, JSONObject json) {
            for (Field field : o.getClass().getFields()) {
                if (field.getAnnotation(ConfigValue.class)!=null) {
                    Class<?> type=field.getType();
                    String name=field.getName();
                    try {
                        if (type.equals(String.class)) json.put(name, field.get(o));
                        else if (type.equals(int.class)) json.put(name, field.getInt(o));
                        else if (type.equals(byte.class)) json.put(name, field.getByte(o));
                        else if (type.equals(long.class)) json.put(name, field.getLong(o));
                        else if (type.equals(short.class)) json.put(name, field.getShort(o));
                        else if (type.equals(boolean.class)) json.put(name, field.getBoolean(o));
                        else if (type.equals(char.class)) json.put(name, field.getChar(o)+"");
                        else if (type.equals(float.class)) json.put(name, field.getFloat(o));
                        else if (type.equals(double.class)) json.put(name, field.getDouble(o));
                        else if (ConfigVariable.class.isAssignableFrom(type))
                            json.put(name, ((ConfigVariable) field.get(o)).get());
                        else if (type.equals(ArrayList.class)) json.put(name, new JSONArray((ArrayList) field.get(o)));
                        else {
                            if (!json.has(name)) json.put(name, new JSONObject());
                            save(field.get(o), json.getJSONObject(name));
                        }
                    } catch (JSONException|IllegalAccessException e) {
                        game.logger.error(e.getClass().getSimpleName()+" trying to save Config var:"+field.getName()+" in json:"+json);
                    }
                }
            }
        }
    }
}
