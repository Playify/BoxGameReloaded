package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.BlockIconDrawButton;
import at.playify.boxgamereloaded.level.FakeLevel;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.network.packet.PacketMove;
import at.playify.boxgamereloaded.network.packet.PacketPaintMenuAction;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;

import java.util.AbstractList;
import java.util.ArrayList;

//Zuständig um sich ums zeichnen vom User zu kümmern
public class PaintHandler extends AbstractList<Paintable> {
    public final FakeLevel fakeLevel;
    public final ArrayList<Paintable> list=new ArrayList<>();
    public boolean quick;
    public boolean zoom;
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
        list.add(new PlayPaint(game));
        list.addAll(game.blocks.list);
        list.add(game.player);
        list.add(new CheckPointPaint(game));
        list.add(new LevelSpawnPaint(game));
    }

    public void handleDrawFingers() {
        if (!draw) {
            wasdrawing=false;
            down=false;
            return;
        }
        if (game.painter.zoom) {
            Finger finger=game.fingers[0];
            if (down!=(finger.down&&!finger.control)) {
                down^=true;
                if (down) {
                    lx=finger.x;
                    ly=finger.y;
                    zooming=finger.x/game.d.getHeight()<1/7f;
                }
            }
            if (down) {
                if (zooming) {
                    float zoom=game.zoom*((ly-finger.y)/game.d.getHeight()+1);
                    ly=finger.y;
                    game.zoom=Utils.clamp(zoom, 0.3f, 5f);
                } else {
                    float w=game.d.getWidth(), h=game.d.getHeight();
                    float x=(lx-finger.x)*game.vars.display_size*game.aspectratio/(w*game.zoom)+game.zoom_x;
                    float y=-(ly-finger.y)*game.vars.display_size/(h*game.zoom)+game.zoom_y;

                    lx=finger.x;
                    ly=finger.y;


                    game.zoom_x=Utils.clamp(x, 0, game.level.sizeX);
                    game.zoom_y=Utils.clamp(y, 0, game.level.sizeY);
                }
            }
        } else {
            down=false;
            if (wasdrawing) {
                boolean drawing=false;
                float w=game.d.getWidth(), h=game.d.getHeight();
                for (Finger finger : game.fingers) {
                    if (finger.down&&!finger.control) {
                        drawing=true;
                        float x=(finger.x-w/2)*game.vars.display_size*game.aspectratio/(w*game.zoom)+game.zoom_x;
                        float y=-(finger.y-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
                        draw(x, y, false, finger);
                    }
                }

                if (!drawing&&wasdrawing&&!(paint() instanceof PlayPaint)) {
                    game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
                }
                if (!game.connection.serverbound.equals(game.player.bound)) {
                    game.connection.sendPacket(new PacketMove(game.player.bound));
                }
                wasdrawing=drawing;
            }
        }
    }

    private void draw(float fx, float fy, boolean click, Finger finger) {
        if (click){
            wasdrawing=true;
        }
        int px=(int) Math.floor(fx);
        int py=(int) Math.floor(fy);
        if (click&&game.level.get(px, py) instanceof BlockIconDrawButton&&!(paint() instanceof PlayPaint)) {
            int meta=game.level.getMeta(px, py);
            int buttonID;
            if (py==-2) buttonID=0;
            else if (px==-2) buttonID=1;
            else if (py==game.level.sizeY+1) buttonID=2;
            else if (px==game.level.sizeX+1) buttonID=3;
            else{
                if (click&&paint.history()) {
                    game.level.saveHistory();
                }
                paint.paint(fx, fy, true, finger);
                return;
            }
            game.level.saveHistory();

            int x=-((buttonID&1)!=0 ? (buttonID&2)-1 : 0);
            int y=-((buttonID&1)!=0 ? 0 : (buttonID&2)-1);
            if (meta==1) {
                if(game.level.sizeX<=0)x=0;
                if(game.level.sizeY<=0)y=0;
                game.level.shift( x, y);
            }else if (meta==0){
                game.level.size(game.level.sizeX+(x!=0?1:0),game.level.sizeY+(y!=0?1:0));
                int xx=x>0 ? 1 : 0;
                int yy=y>0 ? 1 : 0;
                game.level.shift( -xx, -yy);
            }else if (meta==2){
                int xx=x>0 ? 1 : 0;
                int yy=y>0 ? 1 : 0;
                game.level.shift( xx, yy);
                game.level.size(game.level.sizeX+(x!=0?-1:0),game.level.sizeY+(y!=0?-1:0));
                if(game.level.sizeX<=0)xx=0;
                if(game.level.sizeY<=0)yy=0;
            }
            x*=-1;
            y*=-1;

            if (meta==0) {
                game.zoom_x+=x<0 ? 0 : x;
                game.zoom_y+=y<0 ? 0 : y;
            }else if (meta==2) {
                game.zoom_x-=x<0 ? 0 : x;
                game.zoom_y-=y<0 ? 0 : y;
            }
            game.zoom_x=Utils.clamp(game.zoom_x, 0, game.level.sizeX);
            game.zoom_y=Utils.clamp(game.zoom_y, 0, game.level.sizeY);
            game.connection.sendPacketSoon(new PacketPaintMenuAction(meta, buttonID));
            wasdrawing=false;
        }else {
            if (click&&paint.history()) {
                game.level.saveHistory();
            }
            paint.paint(fx, fy, click, finger);
        }
    }

    public void handleFingerState(Finger finger) {
        if (!game.painter.zoom) {
            float ww=game.d.getWidth(), h=game.d.getHeight();
            float x=(finger.x-ww/2)*game.vars.display_size*game.aspectratio/(ww*game.zoom)+game.zoom_x;
            float y=-(finger.y-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
            draw(x, y, true, finger);
        }
    }

    public void paint(Paintable paint) {
        if (paint!=null&&paint.canDraw()) {
            this.paint=paint;
        }
    }

    public Paintable paint() {
        if (!paint.canDraw()) paint=game.blocks.AIR;
        return paint;
    }

    public boolean pause() {
        return draw&&(!(paint instanceof PlayPaint)||game.painter.quick);
    }

    @Override
    public Paintable get(int index) {
        int v=0;
        for (int i=0;i<list.size();i++) {
            if (list.get(i).canDraw()) {
                if (v==index) return list.get(i);
                v++;
            }
        }
        return null;
    }

    @Override
    public int size() {
        int v=0;
        for (int i=0;i<list.size();i++)
            if (list.get(i).canDraw()) v++;
        return v;
    }
}
