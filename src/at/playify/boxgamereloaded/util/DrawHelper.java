package at.playify.boxgamereloaded.util;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.network.packet.PacketMove;

//Zuständig um sich ums zeichnen vom User zu kümmern
public class DrawHelper {
    private BoxGameReloaded game;
    public boolean draw;
    public Block drawblock;
    private boolean wasdrawing;

    public DrawHelper(BoxGameReloaded game) {
        this.game=game;
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
                float aspectratio=game.d.getWidth()/game.d.getHeight();
                float x=(finger.x-w/2)*game.vars.display_size*aspectratio/(w*game.zoom)+game.zoom_x;
                float y=-(finger.y-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
                draw(x, y, false);
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
    }

    private void draw(float fx, float fy, boolean click) {
        wasdrawing=true;
        if (drawblock==null) {
            game.player.bound.setCenter(fx, fy);
            game.player.motionX=game.player.motionY=0;
        } else {
            int x=(int) fx, y=(int) fy;
            if (game.level.get(x, y)==drawblock&&click) {
                game.level.setMeta(x, y, game.level.getMeta(x, y)+1);
            } else {
                game.level.set(x, y, drawblock);
            }
        }
    }

    public void handleFingerState(Finger finger) {
        float ww=game.d.getWidth(), h=game.d.getHeight();
        float aspectratio=game.d.getWidth()/game.d.getHeight();
        float x=(finger.x-ww/2)*game.vars.display_size*aspectratio/(ww*game.zoom)+game.zoom_x;
        float y=-(finger.y-h/2)*game.vars.display_size/(h*game.zoom)+game.zoom_y;
        draw(x, y, true);
    }

    public void setDraw(boolean draw) {
        this.draw=draw;
    }

    public String getBlockString() {
        if (drawblock==null) {
            return "Player";
        } else {
            String name=drawblock.getClass().getSimpleName();
            if (name.startsWith("Block")) {
                return name.substring(5);
            }
            return name;
        }
    }

    public void nextBlock() {
        int i=game.blocks.list.indexOf(drawblock)+1;
        drawblock=game.blocks.list.size()<=i ? null : game.blocks.list.get(i);
    }
}
