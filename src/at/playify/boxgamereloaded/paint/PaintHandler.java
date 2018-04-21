package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.FakeLevel;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.network.packet.PacketMove;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

//Zuständig um sich ums zeichnen vom User zu kümmern
public class PaintHandler {
    public final FakeLevel fakeLevel;
    public final ArrayList<Paintable> list=new ArrayList<>();
    private Paintable paint;
    private final BoxGameReloaded game;
    public boolean draw;
    private boolean wasdrawing;

    public PaintHandler(BoxGameReloaded game) {
        this.game=game;
        fakeLevel=new FakeLevel(game);
        paint=game.player;
        list.add(new MovePaint());
        if (game.vars.scrollPaint) {
            list.add(new ScrollPaint(game));
        }
        list.addAll(game.blocks.list);
        list.add(game.player);
    }

    public void handleDrawFingers() {
        if (!draw) {
            wasdrawing=false;
            return;
        }
        boolean drawing=false;
        for (Finger finger : game.fingers) {
            if (finger.down&&!finger.control) {
                drawing=true;
                float w=game.d.getWidth(), h=game.d.getHeight();
                float x=(finger.getX()-w/2)*game.vars.display_size*game.aspectratio/(w*game.zoom)+game.zoom_x;
                float y=-(finger.getY()-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
                draw(x, y, false, finger);
            }
        }
        if (!drawing&&wasdrawing) {
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

    private void draw(float fx, float fy, boolean click, Finger finger) {
        wasdrawing=true;
        paint.paint(fx, fy, click, finger);
    }

    public void handleFingerState(Finger finger) {
        float ww=game.d.getWidth(), h=game.d.getHeight();
        float x=(finger.getX()-ww/2)*game.vars.display_size*game.aspectratio/(ww*game.zoom)+game.zoom_x;
        float y=-(finger.getY()-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
        draw(x, y, true, finger);
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
        return draw&&(!(paint instanceof MovePaint)||game.gui.drawer.quick);
    }
}
