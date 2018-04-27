package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.FakeLevel;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.network.packet.PacketMove;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;

import java.util.ArrayList;

//Zuständig um sich ums zeichnen vom User zu kümmern
public class PaintHandler {
    public final FakeLevel fakeLevel;
    public final ArrayList<Paintable> list=new ArrayList<>();
    private Paintable paint;
    private final BoxGameReloaded game;
    public boolean draw;
    private boolean wasdrawing;
    private boolean down;
    private boolean zooming;
    private float lx;
    private float ly;

    public PaintHandler(BoxGameReloaded game) {
        this.game=game;
        fakeLevel=new FakeLevel(game);
        paint=game.player;
        list.add(new PlayPaint());
        //list.add(new ScrollPaint(game));
        list.addAll(game.blocks.list);
        list.add(game.player);
        list.add(new SpawnPaint(game));
        list.add(new LevelSpawnPaint(game));
    }

    public void handleDrawFingers() {
        if (!draw) {
            wasdrawing=false;
            down=false;
            return;
        }
        if (game.gui.drawer.zoom) {
            Finger finger=game.fingers[0];
            if (down!=(finger.down&&!finger.control)) {
                down^=true;
                if (down) {
                    lx=finger.getX();
                    ly=finger.getY();
                    zooming=finger.getX()/game.d.getHeight()<1/7f;
                }
            }
            if (down) {
                if (zooming) {
                    float zoom=game.zoom*((ly-finger.getY())/game.d.getHeight()+1);
                    ly=finger.getY();
                    game.zoom=Utils.clamp(zoom, 0.3f, 5f);
                } else {
                    float w=game.d.getWidth(), h=game.d.getHeight();
                    float x=(lx-finger.getX())*game.vars.display_size*game.aspectratio/(w*game.zoom)+game.zoom_x;
                    float y=-(ly-finger.getY())*game.vars.display_size/(h*game.zoom)+game.zoom_y;

                    lx=finger.getX();
                    ly=finger.getY();


                    game.zoom_x=Utils.clamp(x, 0, game.level.sizeX);
                    game.zoom_y=Utils.clamp(y, 0, game.level.sizeY);
                }
            }
        } else {
            down=false;
            boolean drawing=false;
            float w=game.d.getWidth(), h=game.d.getHeight();
            for (Finger finger : game.fingers) {
                if (finger.down&&!finger.control) {
                    drawing=true;
                    float x=(finger.getX()-w/2)*game.vars.display_size*game.aspectratio/(w*game.zoom)+game.zoom_x;
                    float y=-(finger.getY()-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
                    draw(x, y, false, finger);
                }
            }

            if (!drawing&&wasdrawing&&!(paint() instanceof PlayPaint)) {
                game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
            }
            if (!game.connection.serverbound.equals(game.player.bound)) {
                game.connection.sendPacket(new PacketMove(game.player.bound));
                game.connection.serverbound.set(game.player.bound);
            }
            wasdrawing=drawing;
            if (paint instanceof Tickable) {
                ((Tickable) paint).tick();
            }
        }
    }

    private void draw(float fx, float fy, boolean click, Finger finger) {
        wasdrawing=true;
        paint.paint(fx, fy, click, finger);
    }

    public void handleFingerState(Finger finger) {
        if (!game.gui.drawer.zoom) {
            float ww=game.d.getWidth(), h=game.d.getHeight();
            float x=(finger.getX()-ww/2)*game.vars.display_size*game.aspectratio/(ww*game.zoom)+game.zoom_x;
            float y=-(finger.getY()-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
            draw(x, y, true, finger);
        }
    }

    public void setDraw(boolean draw) {
        this.draw=draw;
    }

    public void paint(Paintable paint) {
        if (paint!=null) {
            this.paint=paint;
        }
    }

    public Paintable paint() {
        return paint;
    }

    public boolean pause() {
        return draw&&(!(paint instanceof PlayPaint)||game.gui.drawer.quick);
    }
}
