package at.playify.boxgamereloaded;


import at.playify.boxgamereloaded.block.Blocks;
import at.playify.boxgamereloaded.exceptions.DrawingException;
import at.playify.boxgamereloaded.gui.GuiOverlay;
import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.interfaces.Handler;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.EmptyConnection;
import at.playify.boxgamereloaded.network.packet.PacketFinish;
import at.playify.boxgamereloaded.network.packet.PacketSetPauseMode;
import at.playify.boxgamereloaded.network.packet.PacketSetWorld;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.DrawHelper;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class BoxGameReloaded extends Game {
    public PlayerSP player = new PlayerSP(this);
    public ConnectionToServer connection = new EmptyConnection();
    @SuppressWarnings("WeakerAccess")
    public boolean options;
    public DrawHelper drawer = new DrawHelper(this);
    public float zoom_x;
    public float zoom_y;
    public float zoom = 1.3f;
    public int vertexcount;
    public float aspectratio;
    private boolean pauseKeyDown;
    private boolean optionsKeyDown;
    private RectBound leveldrawbound = new RectBound();
    private boolean prevPauseState = false;
    private ArrayList<String> txt = new ArrayList<>();
    private int[] lastframes = new int[10];
    private int lastframeindex;
    private long lastframetime;
    private int[] lastticks=new int[10];
    private int lasttickindex;
    private long lastticktime;
    private double tps;
    private DecimalFormat dm = new DecimalFormat("0.0");
    public GuiOverlay gui;

    {
        dm.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    public BoxGameReloaded(Handler handler) {
        super(handler);
    }

    private StringBuilder str = new StringBuilder();

    //Fingerstatus geändert gedrückt/nicht gedrückt
    @Override
    public void fingerStateChanged(Finger finger) {
        float w = d.getHeight() / 6;
        if (finger.down) {
            finger.control = gui.click(finger);
            if (!finger.control) {
                if (drawer.draw) {
                    drawer.handleFingerState(finger);
                }
            } else {
                gui.fingerStateChanged(finger);
            }
        } else {
            if (!finger.control) {
                paused = false;
                pauseLock.unlock();
            }
            finger.control = false;
        }
        pauseLock.unlock();
    }

    private void mainMenu() {
        if (connection != null) {
            connection.leaveWorld();
        }
        gui.openMainMenu();
    }

    //Spieltick ausführen
    @Override
    public void tick() {
        boolean pause = false;
        if (connection != null) {
            pause = connection.isPaused(false);
            if (prevPauseState != (paused | options)) {
                prevPauseState = (paused | options);
                if (connection.userpause) {
                    connection.sendPacket(new PacketSetPauseMode((paused | options) ? 3 : 2));
                }
            }
            connection.handleSoon();
            if (!connection.isPaused(false)) {
                Player[] players = connection.players;
                for (Player player1 : players) {
                    player1.tick();
                }
                drawer.handleDrawFingers();
                if (player != null) {
                    player.tick();
                }
                pause = false;
            }
        }
        //Pausestatus bekommen
        boolean lock=gui.tick();

        if (((connection==null||connection.userpause) ? gui.backgroundState()==0 : !connection.pause)&&player!=null) {
            float x = Utils.clamp(player.bound.cx(), 0, level.sizeX) - zoom_x;
            float y = Utils.clamp(player.bound.cy(), 0, level.sizeY) - zoom_y;
            if (Math.abs(x)<0.01f) {
                zoom_x+=x;
            } else {
                zoom_x+=x/20f;
                lock=false;
            }
            if (Math.abs(y)<0.01f) {
                zoom_y+=y;
            } else {
                zoom_y+=y/20f;
                lock=false;
            }
            if (Math.abs(zoom-vars.zoom_level)<0.01f) {
                zoom=vars.zoom_level;
            } else {
                zoom+=(vars.zoom_level-zoom)/20f;
                lock=false;
            }
        }
        //Ticks zählen
        ticker.ticks++;
        lasttickindex=(lasttickindex+1)%lastticks.length;
        lastticks[lasttickindex]=(int) (-lastticktime+(lastticktime=System.nanoTime()));
        long l=0;
        for (int lastframe : lastticks) {
            l+=lastframe;
        }
        tps=lastframes.length*1000000000d/l;

        //wenn nötig locken
        if (pause&&(prevPauseState==(paused||options))&&lock) {
            Arrays.fill(lastticks, 0);
            tps=0;
            pauseLock.lock();
        }
    }

    //Spiel zeichnen
    @Override
    public void draw() {
        if (vars.tickOnDraw) {
            tick();
        }
        if (!canDraw) {
            return;
        }
        try {
            d.startDrawing();
            d.fill(0xFF33B5E5);
            if (vars.debug.viewback) {
                d.scale(1, 1, -1);
            }
            aspectratio = d.getWidth() / d.getHeight();
            d.scale(1 / aspectratio, 1);
            d.pushMatrix();
            d.scale(1 / (vars.display_size));
            if (((connection==null||connection.userpause) ? gui.backgroundState()==0 : !connection.pause)&&player!=null&&vars.instant_zoom) {
                zoom_x = Utils.clamp(player.bound.cx(), 0, level.sizeX);
                zoom_y = Utils.clamp(player.bound.cy(), 0, level.sizeY);
            }
            d.scale(zoom);
            d.translate(-zoom_x + (vars.display_size * aspectratio / 2f) / zoom, -zoom_y + (vars.display_size / 2f) / zoom);
            if (vars.geometry_dash) {
                if (vars.cubic) {
                    float v = vars.display_size;
                    d.cube(zoom_x - v, -1, 0, 2 * v, 1, 1, 0xFFFFFF00, true, false, true, false);
                    d.cube(zoom_x - v, level.sizeY, 0, 2 * v, 1, 1, 0xFFFFFF00, true, false, true, false);
                    if (drawer.draw) {
                        d.cube(-.1f, 0, .9f, .1f, level.sizeY, .1f, 0xFFFFFF00, false, true, false, true);
                        d.cube(level.sizeX, 0, .9f, .1f, level.sizeY, .1f, 0xFFFFFF00, false, true, false, true);
                    }
                } else {
                    float v = vars.display_size;
                    d.rect(zoom_x - v, -1, 2 * v, 1, 0xFFFFFF00);
                    d.rect(zoom_x - v, level.sizeY, 2 * v, 1, 0xFFFFFF00);
                    if (drawer.draw) {
                        d.rect(-.1f, 0, .1f, level.sizeY, 0xFFFFFF00);
                        d.rect(level.sizeX, 0, .1f, level.sizeY, 0xFFFFFF00);
                    }
                }
            } else {
                if (vars.cubic) {
                    d.cube(-1, -1, 0, level.sizeX + 2, 1, 1, 0xFFFFFF00, true, false, true, false);
                    d.cube(-1, -1, 0, 1, level.sizeY + 2, 1, 0xFFFFFF00, false, true, false, true);
                    d.cube(-1, level.sizeY, 0, level.sizeX + 2, 1, 1, 0xFFFFFF00, true, false, true, false);
                    d.cube(level.sizeX, -1, 0, 1, level.sizeY + 2, 1, 0xFFFFFF00, false, true, false, true);
                } else {
                    d.rect(-1, -1, level.sizeX + 2, 1, 0xFFFFFF00);
                    d.rect(-1, -1, 1, level.sizeY + 2, 0xFFFFFF00);
                    d.rect(-1, level.sizeY, level.sizeX + 2, 1, 0xFFFFFF00);
                    d.rect(level.sizeX, -1, 1, level.sizeY + 2, 0xFFFFFF00);
                }
            }
            float mx = vars.display_size / 2f * aspectratio;
            float my = vars.display_size / 2f;
            level.draw(leveldrawbound.set(zoom_x - mx, zoom_y - my, mx * 2, my * 2));

            d.popMatrix();
            drawGui();

        } catch (DrawingException e) {
            Game.report(e);
        } finally {
            d.stopDrawing();
        }

    }

    //draw game gui
    private void drawGui() {
        txt.clear();
        d.clearDepth();
        //Debug Text anzeigen
        if (drawer.draw) {
            txt.add("Draw:" + drawer.getBlockString());
        }
        logFrame();

        //Gui zeichnen
        d.pushMatrix();
        d.translate(0, 0, -0.025f);
        gui.draw();
        d.popMatrix();
    }

    //Spiel info auf Bildschirm oben links anzeigen
    private void logFrame() {
        lastframeindex = (lastframeindex + 1) % lastframes.length;
        lastframes[lastframeindex] = (int) (-lastframetime + (lastframetime = System.nanoTime()));
        long l = 0;
        for (int lastframe : lastframes) {
            l += lastframe;
        }
        str.setLength(0);
        str.append(dm.format(tps)).append("TPS,");
        str.append(dm.format(lastframes.length * 1000000000d / l)).append("FPS,").append(vertexcount).append("Verts");
        txt.add(str.toString());
        str.setLength(0);
        str.append("POS:").append(player.bound.toSimpleString(",", true));
        txt.add(str.toString());
        float h = .95f;
        for (String s : txt) {
            d.drawString(s, .18f, h, .05f);
            h -= .06f;
        }
        vertexcount = 0;
    }

    //Objekte initialisieren
    public void init() {
        ticker = new TickThread(this);
        blocks = new Blocks();
        blocks.init(this);
        level = new Level(this);
        level.setSize(20, 20);
    }

    @Override
    public void start() {
        super.start();
        gui = new GuiOverlay(this);
    }

    @Override
    public void runcmd(String text) {
        //TODO
        System.out.println("RUNCMD:" + text);
    }

    //Tastaturstatusänderungen
    @Override
    protected void keyStateChanged(int keyChar) {
        pauseLock.unlock();
        if (keyChar == 'c' && vars.debug.console && !handler.isKeyboardVisible()) {
            handler.setKeyboardVisible(true);
        }
        if (keyChar == 'p') {
            if (paused != keys['p']) {
                if (!pauseKeyDown) {
                    paused ^= true;
                    pauseKeyDown = paused;
                    pauseLock.unlock();
                } else {
                    pauseKeyDown = false;
                }
            } else {
                pauseKeyDown = false;
            }
        }
        if (keyChar == 'o') {
            if (options != keys['o']) {
                if (!optionsKeyDown) {
                    options ^= true;
                    optionsKeyDown = options;
                    pauseLock.unlock();
                } else {
                    optionsKeyDown = false;
                }
            } else {
                optionsKeyDown = false;
            }
        }
        if (keys[keyChar]) {
            if (Character.isDigit(keyChar)) {
                joinWorld("" + ((char) keyChar));
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void joinWorld(String name) {
        connection.sendPacketSoon(new PacketSetWorld(name));
    }

    public void finishLevel() {
        player.bound.set(level.spawnPoint);
        //TODO save Level finished to config. (using handler)
        //TODO send PacketSetWorld to next level
        connection.sendPacket(new PacketFinish());
    }
}
