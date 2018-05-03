package at.playify.boxgamereloaded;


import at.playify.boxgamereloaded.block.Blocks;
import at.playify.boxgamereloaded.commands.CMDHandler;
import at.playify.boxgamereloaded.gui.GuiOverlay;
import at.playify.boxgamereloaded.interfaces.*;
import at.playify.boxgamereloaded.interfaces.exceptions.DrawingException;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.network.connection.ConnectionSinglePlayer;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.EmptyConnection;
import at.playify.boxgamereloaded.network.packet.PacketFinish;
import at.playify.boxgamereloaded.network.packet.PacketHello;
import at.playify.boxgamereloaded.network.packet.PacketSetPauseMode;
import at.playify.boxgamereloaded.network.packet.PacketSetWorld;
import at.playify.boxgamereloaded.paint.PaintHandler;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class BoxGameReloaded extends Game {
    public VariableContainer vars;
    public final PlayerSP player=new PlayerSP(this);
    public final CMDHandler cmd;
    public ConnectionToServer connection = new EmptyConnection();
    public PaintHandler painter;
    public float zoom_x;
    public float zoom_y;
    public float zoom = 1.3f;
    public int vertexcount;
    public float aspectratio;
    public VertexData vertex=new VertexData();
    public SkinData skin=new SkinData(this);
    public TailData tail=new TailData(this);
    private boolean pauseKeyDown;
    private boolean optionsKeyDown;
    private RectBound leveldrawbound = new RectBound();
    private boolean prevPauseState = false;
    private ArrayList<String> txt = new ArrayList<>();
    @SuppressWarnings("CanBeFinal")
    private int[] lastframes = new int[10];
    private int lastframeindex;
    private long lastframetime;
    @SuppressWarnings("CanBeFinal")
    private int[] lastticks=new int[10];
    private int lasttickindex;
    long lastticktime;
    private double tps;
    private DecimalFormat dm = new DecimalFormat("0.0");
    public GuiOverlay gui;

    {
        dm.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    public BoxGameReloaded(Handler handler) {
        super(handler);
        paused=true;
        vars=new VariableContainer(this);
        vars.loader.load();
        ticker=new TickThread(this);
        blocks=new Blocks();
        blocks.init(this);
        level=new Level(this);
        level.setSize(20, 20);
        painter=new PaintHandler(this);
        cmd=new CMDHandler(this);
    }

    private StringBuilder str = new StringBuilder();

    //Fingerstatus geändert gedrückt/nicht gedrückt
    @Override
    public void fingerStateChanged(Finger finger) {
        if (finger.down) {
            finger.control = gui.click(finger);
            if (!finger.control) {
                if (painter.draw&&connection.pauseCount==0) {
                    painter.handleFingerState(finger);
                }
            }
        } else {
            if (!finger.control) {
                if (gui.isOptionsVisible()) {
                    gui.closeOptions();
                } else {
                    paused=false;
                }
                pauseLock.unlock();
            }
            finger.control = false;
        }
        pauseLock.unlock();
    }

    //Spieltick ausführen
    @Override
    public void tick() {
        if (connection.isClosed()) {
            cmd.error("Connection closed! reconnecting to SinglePlayer");
            connection.close();
            connection=new ConnectionSinglePlayer(this);
            connection.sendPacket(new PacketHello());
            connection.sendPacket(new PacketSetWorld(vars.world));
        }
        boolean pause = false;
        if (connection != null) {
            pause = connection.isPaused(false);
            if (prevPauseState!=(paused||gui.isOptionsVisible())) {
                prevPauseState=(paused||gui.isOptionsVisible());
                if (connection.userpause) {
                    connection.sendPacket(new PacketSetPauseMode((paused||gui.isOptionsVisible()) ? 3 : 2));
                }
            }
            connection.handleSoon();
            if (!connection.isPaused(false)) {
                Player[] players = connection.players;
                for (Player player1 : players) {
                    player1.tick();
                }
                painter.handleDrawFingers();
                player.tick();
                pause = false;
            }
        }
        //Pausestatus bekommen
        boolean lock=gui.tick();

        if ((connection==null||connection.userpause ? gui.backgroundState()==0 : !connection.pause)&&(!painter.pause())) {
            float shouldX=Utils.clamp(player.bound.cx(), 0, level.sizeX);
            float x=shouldX-zoom_x;
            float shouldY=Utils.clamp(player.bound.cy(), 0, level.sizeY);
            float y=shouldY-zoom_y;
            if (Math.abs(x)<0.01f) {
                zoom_x+=x;
            } else {
                float v=aspectratio*(vars.display_size-2-player.bound.w())/(2*zoom);
                zoom_x=Utils.clamp(zoom_x+x/20f, shouldX-v, shouldX+v);
                lock=false;
            }
            if (Math.abs(y)<0.01f) {
                zoom_y+=y;
            } else {
                float v=(vars.display_size-2-player.bound.h())/(2*zoom);
                zoom_y=Utils.clamp(zoom_y+y/20f, shouldY-v, shouldY+v);
                lock=false;
            }

            float shouldZoom=vars.zoom_level*0.8f/((player.bound.w()+player.bound.h())/2);
            if (Math.abs(this.zoom-shouldZoom)<0.01f) {
                this.zoom=shouldZoom;
            } else {
                this.zoom+=(shouldZoom-this.zoom)/20f;
                lock=false;
            }
        }
        //Ticks zählen
        lasttickindex=(lasttickindex+1)%lastticks.length;
        lastticks[lasttickindex]=(int) (-lastticktime+(lastticktime=System.nanoTime()));
        long l=0;
        for (int lastframe : lastticks) {
            l+=lastframe;
        }
        tps=lastframes.length*1000000000d/l;

        //wenn nötig locken
        if (pause&&(prevPauseState==(paused||gui.isOptionsVisible()))&&lock) {
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
        if (d==null||!d.ready()) {
            return;
        }
        try {
            d.startDrawing();
            d.fill(0xFF33B5E5);
            if (vars.debug.viewback) d.scale(1, 1, -1);
            aspectratio = d.getWidth() / d.getHeight();
            d.scale(1 / aspectratio, 1);
            d.pushMatrix();
            d.scale(1 / (vars.display_size));
            if (((connection==null||connection.userpause) ? gui.backgroundState()==0 : !connection.pause)&&vars.instant_zoom&&(!painter.pause())) {
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
                    if (painter.draw) {
                        d.cube(-.1f, 0, .9f, .1f, level.sizeY, .1f, 0xFFFFFF00, false, true, false, true);
                        d.cube(level.sizeX, 0, .9f, .1f, level.sizeY, .1f, 0xFFFFFF00, false, true, false, true);
                    }
                } else {
                    float v = vars.display_size;
                    d.rect(zoom_x - v, -1, 2 * v, 1, 0xFFFFFF00);
                    d.rect(zoom_x - v, level.sizeY, 2 * v, 1, 0xFFFFFF00);
                    if (painter.draw) {
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
            float mx=(vars.display_size/zoom)/2f*aspectratio+1;
            float my=(vars.display_size/zoom)/2f+1;
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
        d.clearDepth();
        //Debug Text anzeigen
        logFrame();

        //Gui zeichnen
        d.pushMatrix();
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
        txt.clear();
        str.setLength(0);
        str.append(dm.format(tps)).append("TPS,");
        str.append(dm.format(lastframes.length * 1000000000d / l)).append("FPS,").append(vertexcount).append("Verts");
        txt.add(str.toString());
        str.setLength(0);
        str.append(Borrow.info());
        txt.add(str.toString());
        str.setLength(0);
        str.append("POS:").append(player.bound.toSimpleString(",", true));
        txt.add(str.toString());
        float h=.94f;
        for (String s : txt) {
            d.drawString(s, .19f, h, .05f);
            h -= .06f;
        }
        vertexcount = 0;
    }

    @Override
    public void start() {
        gui = new GuiOverlay(this);
        super.start();
    }

    @Override
    public void runcmd(String text) {
        cmd.run(text);
    }

    //Tastaturstatusänderungen
    @Override
    protected void keyStateChanged(int keyChar) {
        pauseLock.unlock();
        if (keyChar==257) {//ESC key
            if (painter.draw) keyChar='o';
            else if (paused) keyChar='p';
            else if (gui.isOptionsVisible()) keyChar='o';
            else keyChar='p';
            keys[keyChar]=keys[257];
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
            if (gui.isOptionsVisible()!=keys['o']) {
                if (!optionsKeyDown) {
                    if (gui.isOptionsVisible()) {
                        gui.closeOptions();
                        optionsKeyDown=false;
                    } else {
                        gui.openOptions();
                        optionsKeyDown=true;
                    }
                    pauseLock.unlock();
                } else {
                    optionsKeyDown = false;
                }
            } else {
                optionsKeyDown = false;
            }
        }
        if (keyChar=='c'&&!keys['c']&&vars.debug.console) {
            handler.setKeyboardVisible(true);
        }
        if (keyChar=='v'&&!keys['v']&&(vars.debug.console||vars.world.startsWith("paint_"))&&!gui.isMainMenuVisible()) {
            painter.draw^=true;
        }
        if (keyChar=='q'&&!keys['q']&&(vars.debug.console||vars.world.startsWith("paint_"))&&!gui.isMainMenuVisible()&&painter.draw) {
            painter.quick^=true;
        }
        if (keys[keyChar]) {
            if (Character.isDigit(keyChar)) {
                joinWorld("paint_"+((char) keyChar));
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void joinWorld(String name) {
        connection.sendPacketSoon(new PacketSetWorld(name));
    }

    public void finishLevel() {
        player.bound.set(level.spawnPoint);
        vars.finishedLevels.add(vars.world);
        vars.loader.save();
        connection.sendPacket(new PacketFinish());
    }
}
