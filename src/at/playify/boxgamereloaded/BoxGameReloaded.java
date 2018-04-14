package at.playify.boxgamereloaded;


import at.playify.boxgamereloaded.block.Blocks;
import at.playify.boxgamereloaded.exceptions.DrawingException;
import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.interfaces.Handler;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
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
import java.util.Locale;

public class BoxGameReloaded extends Game {

    public PlayerSP player=new PlayerSP(this);
    public ConnectionToServer connection=new EmptyConnection();
    @SuppressWarnings("WeakerAccess")
    public boolean options;
    public DrawHelper drawer=new DrawHelper(this);
    public float zoom_x;
    public float zoom_y;
    public float zoom=1.3f;
    public int backgroundState;
    public int vertexcount;
    private RectBound buttonbound=new RectBound();
    private RectBound leveldrawbound=new RectBound();
    private float pauseState=0;
    private float optionsState=0;
    private boolean pauseKeyDown;
    private boolean optionsKeyDown;
    private float aspectratio;
    private int settingsRotate;
    private boolean prevPauseState=false;
    private ArrayList<String> txt=new ArrayList<>();
    private int[] lastframes=new int[10];
    private int lastframeindex;
    private long lastframetime;
    private DecimalFormat dm=new DecimalFormat("0.0");

    {
        dm.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    }

    public BoxGameReloaded(Handler handler) {
        super(handler);
    }

    //Fingerstatus geändert gedrückt/nicht gedrückt
    @Override
    public void fingerStateChanged(Finger finger) {
        float w=d.getHeight()/6;
        if (finger.down) {
            finger.control=finger.collide(buttonbound.set(0, 0, w, w))||finger.collide(buttonbound.set(d.getWidth()-w, 0))||finger.collide(buttonbound.move(-4/3f*w, 0));
            if (!finger.control) {
                if (drawer.draw) {
                    drawer.handleFingerState(finger);
                }
            } else {
                if (finger.collide(buttonbound.set(0, 0, w, w))) {
                    drawer.setDraw(!drawer.draw);
                }
                if (finger.collide(buttonbound.set(d.getWidth()-w, 0))) {
                    if (drawer.draw) {
                        int i=blocks.list.indexOf(drawer.drawblock)+1;
                        drawer.drawblock=blocks.list.size()<=i?null:blocks.list.get(i);
                        System.out.println("Drawing with:"+(drawer.drawblock==null ? "PLAYER" : drawer.drawblock.getClass().getSimpleName()));
                    } else {
                        if (!paused) {
                            paused=true;
                            options=false;
                        } else {
                            paused=false;
                        }
                    }
                }
                if (finger.collide(buttonbound.move(-4/3f*w, 0))) {
                    if (paused||options) {
                        mainMenu();
                    } else {
                        if (player!=null) {
                            player.killedByButton();
                        }
                    }
                }
            }
        } else {
            if (!finger.control) {

                paused=false;
                pauseLock.unlock();
            }
            finger.control=false;
        }
        pauseLock.unlock();
    }

    private void mainMenu() {
        if (connection!=null) {
            connection.leaveWorld();
        }
    }

    //Spieltick ausführen
    @Override
    public void tick() {
        boolean pause=false;
        if (connection!=null) {
            pause=connection.isPaused(false);
            if (prevPauseState!=(paused|options)) {
                prevPauseState=(paused|options);
                if (connection.userpause) {
                    connection.sendPacket(new PacketSetPauseMode((paused|options) ? 3 : 2));
                }
            }
            connection.handleSoon();
            if (!connection.isPaused(false)) {
                Player[] players=connection.players;
                for(Player player1 : players) {
                    player1.tick();
                }
                drawer.handleDrawFingers();
                if (player!=null) {
                    player.tick();
                }
                pause=false;
            }
        }
        //Pausestates und Optionstates bearbeiten
        if (options) {
            optionsState=Math.min(1f, optionsState+1/8f);
            pauseState=Math.max(0, pauseState-1/8f);
        } else {
            optionsState=Math.max(0, optionsState-1/8f);
            if (paused||(connection==null||connection.isPaused(true))) {
                pauseState=Math.min(1f, pauseState+1/8f);
            } else {
                pauseState=Math.max(0, pauseState-1/8f);
            }
        }
        if (paused||options||(connection==null||connection.isPaused(true)))  {
            backgroundState=Math.min(255, backgroundState+32);
            if (backgroundState!=255) {
                pause=false;
            }
        } else {
            backgroundState=Math.max(0, backgroundState-32);
            pause=false;
        }
        int v=5;
        if (settingsRotate>0) {
            settingsRotate+=v;
            if (settingsRotate>=90) settingsRotate=0;
        } else if (keys['o']||keys['O']) {
            settingsRotate=v;
        } else if (d!=null) {
            float w=d.getHeight()/6;
            for(Finger finger : fingers) {
                if (finger.control&&finger.x<=w&&finger.y<=w) {
                    settingsRotate=v;
                    break;

                }
            }
        }
        //wenn nötig locken
        if ((pause&&(optionsState==1||pauseState==1))&&settingsRotate==0&&(prevPauseState==(paused|options))) {
            pauseLock.lock();
        }
        if (((connection == null || connection.userpause) ? backgroundState == 0 : !connection.pause) && player != null) {
            float x = Utils.clamp(player.bound.cx(), 0, level.sizeX) - zoom_x;
            float y = Utils.clamp(player.bound.cy(), 0, level.sizeY) - zoom_y;
            zoom_x += x / 20f;
            zoom_y += y / 20f;
            zoom -= (zoom - (vars.zoom_level)) / 20f;
        }
        ticker.ticks++;
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
            aspectratio=d.getWidth()/d.getHeight();
            d.scale(1/aspectratio, 1);
            d.pushMatrix();
            d.scale(1/(vars.display_size));
            if (((connection == null || connection.userpause) ? backgroundState == 0 : !connection.pause) && player != null && vars.instant_zoom) {
                zoom_x = Utils.clamp(player.bound.cx(), 0, level.sizeX);
                zoom_y = Utils.clamp(player.bound.cy(), 0, level.sizeY);

                zoom-=(zoom-(vars.zoom_level))/20f;
            }
            d.scale(zoom);
            d.translate(-zoom_x+(vars.display_size*aspectratio/2f)/zoom, -zoom_y+(vars.display_size/2f)/zoom);
            if (vars.cubic) {
                d.cube(-1, -1, 0, level.sizeX+2, 1, 1, 0xFFFFFF00, true, false, true, false);
                d.cube(-1, -1, 0, 1, level.sizeY+2, 1, 0xFFFFFF00, false, true, false, true);
                d.cube(-1, level.sizeY, 0, level.sizeX+2, 1, 1, 0xFFFFFF00, true, false, true, false);
                d.cube(level.sizeX, -1, 0, 1, level.sizeY+2, 1, 0xFFFFFF00, false, true, false, true);
            } else {
                d.rect(-1, -1, level.sizeX+2, 1, 0xFFFFFF00);
                d.rect(-1, -1, 1, level.sizeY+2, 0xFFFFFF00);
                d.rect(-1, level.sizeY, level.sizeX+2, 1, 0xFFFFFF00);
                d.rect(level.sizeX, -1, 1, level.sizeY+2, 0xFFFFFF00);
            }
            float mx=vars.display_size/2f*aspectratio;
            float my=vars.display_size/2f;
            level.draw(leveldrawbound.set(zoom_x-mx, zoom_y-my, mx*2, my*2));

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
        final float six=1/6f, up=1-six, pausex=aspectratio-six;
        if (drawer.draw) {
            txt.add("Draw:"+drawer.getBlockString());
        }
        logFrame();

        //Darken Background
        if (backgroundState!=0) {
            d.depth(false);
            this.d.rect(-1, -1, aspectratio+2, 3, ((int) (0.3*backgroundState)<<24));
            d.depth(true);
        }

        //Draw Main Menu/Restart Button
        d.pushMatrix();
        final float h=0.025f*6;
        d.translate(pausex-5/36f, up, -h/12f);
        d.rotate(180*backgroundState/255f, 0, 1, 0);
        d.translate(0, 0, h/12f);
        d.scale(1/6f);
        if (backgroundState<=150) {
            d.depth(false);
            d.rect(-1/2f+1/14f, 1/14f, 6/7f, 6/7f, 0xB2FF0000);
            d.depth(true);
        }
        d.cube(-1/2f, 0, -h, 1, 1/7f, h, 0xFF005C7A, true, true, true, true, true, true);
        d.cube(-1/2f, 6/7f, -h, 1, 1/7f, h, 0xFF005C7A, true, true, true, true, true, true);
        d.cube(-1/2f, 1/7f, -h, 1/7f, 5/7f, h, 0xFF005C7A, false, true, false, true, true, true);
        d.cube(5/14f, 1/7f, -h, 1/7f, 5/7f, h, 0xFF005C7A, false, true, false, true, true, true);
        if (backgroundState>150) {
            d.cube(-1f/2+2f/5, 1f/5-1/7f, -h, 1f/5, 1f/5+1/7f, h, 0xFF005C7A, true, true, false, true, false, true);
            d.cube(-1f/2+2f/5, 3f/5, -h, 1f/5, 1f/5+1/7f, h, 0xFF005C7A, false, true, true, true, false, true);
        } else {
            float v=0.1f;
            d.translate(0, 1/2f, 0);
            d.rotate(45, 0, 0, 1);
            float a=.15f, b=.3f;
            for(int i=0; i<4; i++) {
                d.rotate(90, 0, 0, 1);
                d.cube(v, v, -h, b, a, h, 0xFF005C7A, true, true, true, true);
                d.cube(v, v, -h, a, b, h, 0xFF005C7A, true, true, true, true);
            }
            d.rotate(-45, 0, 0, 1);
        }
        d.popMatrix();

        if (pauseState!=0) {
            //d.rect(0,0,aspectratio,1,0xFF000000);
            d.pushMatrix();
            d.translate(aspectratio*.5f, .18f);
            if (connection==null||(connection.userpause&&paused&&!connection.pause)) {
                d.depth(false);
                d.drawStringCenter("Paused", 0, -.4f+pauseState*.4f, .15f);
                d.depth(true);
            } else if (connection.pause) {
                d.depth(false);
                d.drawStringCenter("Paused", 0, -.4f+pauseState*.4f, .15f);
                d.drawStringCenter("by Server", 0, -.5f+pauseState*.4f, .10f);
                d.depth(true);
            }
            d.translate(0, .4f, -.3f);

            d.scale(pauseState);

            long angle=(System.currentTimeMillis()/20)%720;
            d.rotate(angle, 0, 1, 0);
            d.translate(0, ((float) Math.sin(Math.toRadians(angle*2)))*.04f);
            d.scale(.15f, .15f);
            float v=0.03f;
            float[] verts={
                    -1, -1, v,
                    -1, 1, v,
                    1, 0, v
            };
            d.vertex(verts, 0xFF005C7A, 1);
            //bottom
            verts[4]=-1;
            verts[5]=-v;
            d.vertex(verts, 0xFF005C7A, 0.8f);
            verts[0]=1;
            verts[1]=0;
            verts[2]=-v;
            d.vertex(verts, 0xFF005C7A, 0.8f);
            //top
            verts[3]=-1;
            verts[4]=1;
            verts[5]=-v;
            d.vertex(verts, 0xFF005C7A, 0.9f);
            verts[0]=-1;
            verts[1]=1;
            verts[2]=v;
            d.vertex(verts, 0xFF005C7A, 0.9f);
            //wall
            verts[6]=-1;
            verts[7]=-1;
            verts[8]=v;
            d.vertex(verts, 0xFF005C7A, 0.95f);
            verts[0]=-1;
            verts[1]=-1;
            verts[2]=-v;
            d.vertex(verts, 0xFF005C7A, 0.95f);
            verts[6]=1;
            verts[7]=0;
            verts[8]=-v;
            d.vertex(verts, 0xFF005C7A, 1);
            d.popMatrix();
        }

        //Draw Pause Button
        d.pushMatrix();
        d.translate(pausex+six/2, up, 0);
        d.scale(1/6f);
        d.cube(-1/2f, 0, -h, 1, 1/7f, h, 0xFF005C7A, true, true, true, true);
        d.cube(-1/2f, 6/7f, -h, 1, 1/7f, h, 0xFF005C7A, true, true, true, true);
        d.cube(-1/2f, 1/7f, -h, 1/7f, 5/7f, h, 0xFF005C7A, false, true, false, true);
        d.cube(5/14f, 1/7f, -h, 1/7f, 5/7f, h, 0xFF005C7A, false, true, false, true);
        if (pauseState==0) {
            d.cube(-1/10f, 1/5f-1/7f, -h, 1/5f, 3/5f+2/7f, h, 0xFF005C7A, true, true, true, true);
        } else {
            d.translate(0, 1/2f);
            float v=1-.5f*pauseState;
            for(int i=0; i<2; i++) {
                d.pushMatrix();
                d.cube(-1/10f, pauseState/4f, -h, 1/5f, 3/7f*v, h, 0xFF005C7A, true, true, true, true);
                d.translate(1/7f-1/2f, 1/7f-1/2f);
                d.rotate(26.565f*pauseState, 0, 0, 1);
                d.cube(0, -1/7f, -h, 6/7f, 1/7f, h, 0xFF005C7A, true, false, false, false);
                d.cube(2.9f/7f, -2.2f/7f*pauseState, -h, 2.2f/7f, 1.2f/7f*pauseState, h, 0xFF005C7A, false, false, false, false);
                d.popMatrix();
                d.scale(1, -1, 1);
            }
        }
        d.popMatrix();

        //Draw Settings Button
        d.pushMatrix();
        d.translate(six/2, up, 0);
        d.scale(1/6f);
        d.cube(-1/2f, 0, -h, 1, 1/7f, h, 0xFF005C7A, true, false, true, false);
        d.cube(-1/2f, 6/7f, -h, 1, 1/7f, h, 0xFF005C7A, true, false, true, false);
        d.cube(-1/2f, 0, -h, 1/7f, 1, h, 0xFF005C7A, false, true, false, true);
        d.cube(5/14f, 0, -h, 1/7f, 1, h, 0xFF005C7A, false, true, false, true);
        d.translate(0, 1/2f);
        d.rotate(-settingsRotate, 0, 0, 1);
        for(int i=0; i<4; i++) {
            d.cube(-3/14f, -3/14f, -h, 1/7f, 3/7f, h, 0xFF005C7A, false, true, false, true);
            d.rotate(90, 0, 0, 1);
        }

        d.popMatrix();
    }
    private StringBuilder str=new StringBuilder();

    //Spiel info auf Bildschirm oben links anzeigen
    private void logFrame() {
        lastframeindex=(lastframeindex+1)%lastframes.length;
        lastframes[lastframeindex]=(int) (-lastframetime+(lastframetime=System.nanoTime()));
        long l=0;
        for(int lastframe : lastframes) {
            l+=lastframe;
        }
        str.setLength(0);
        str.append(dm.format(lastframes.length*1000000000d/l)).append("FPS,").append(vertexcount).append("Verts");
        txt.add(str.toString());
        str.setLength(0);
        str.append("POS:").append(player.bound.toSimpleString(",", true));
        txt.add(str.toString());
        float h=.95f;
        for(String s : txt) {
            d.drawString(s, .18f, h, .05f);
            h-=.06f;
        }
        vertexcount=0;
    }

    //Objekte initialisieren
    public void init() {
        ticker=new TickThread(this);
        blocks=new Blocks();
        blocks.init(this);
        level=new Level(this);
        level.setSize(20, 20);
    }

    //Tastaturstatusänderungen
    @Override
    protected void keyStateChanged(int keyChar) {
        pauseLock.unlock();
        if (keyChar=='p') {
            if (paused!=keys['p']) {
                if (!pauseKeyDown) {
                    paused^=true;
                    if (paused) options=false;
                    pauseKeyDown=paused;
                    pauseLock.unlock();
                } else {
                    pauseKeyDown=false;
                }
            } else {
                pauseKeyDown=false;
            }
        }
        if (keyChar=='o') {
            if (options!=keys['o']) {
                if (!optionsKeyDown) {
                    options^=true;
                    optionsKeyDown=options;
                    pauseLock.unlock();
                } else {
                    optionsKeyDown=false;
                }
            } else {
                optionsKeyDown=false;
            }
        }
        if (keys[keyChar]){
            if (Character.isDigit(keyChar)){
                joinWorld(""+((char)keyChar));
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void joinWorld(String name){
        connection.sendPacketSoon(new PacketSetWorld(name));
    }

    public void finishLevel() {
        //TODO save Level finished to config. (using handler)
        connection.sendPacket(new PacketSetWorld());
    }
}
