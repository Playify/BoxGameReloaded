package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;

public class ScrollPaint implements Paintable, Tickable {
    private final BoxGameReloaded game;
    private float x, y;

    ScrollPaint(BoxGameReloaded game) {
        this.game=game;
    }

    @Override
    public void draw(int data) {
        //TODO
    }

    @Override
    public String name(int data) {
        return "Scroll";
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
        this.x=x;
        this.y=y;
    }

    public void tick() {
        float shouldX=Utils.clamp(this.x, 0, game.level.sizeX);
        float x=shouldX-game.zoom_x;
        float shouldY=Utils.clamp(this.y, 0, game.level.sizeY);
        float y=shouldY-game.zoom_y;
        if (Math.abs(x)<0.01f) {
            game.zoom_x+=x;
        } else {
            game.zoom_x=game.zoom_x+x/20f;
        }
        if (Math.abs(y)<0.01f) {
            game.zoom_y+=y;
        } else {
            game.zoom_y=game.zoom_y+x/20f;
        }

        float shouldZoom=game.vars.zoom_level*0.8f/((game.player.bound.w()+game.player.bound.h())/2);
        if (Math.abs(this.game.zoom-shouldZoom)<0.01f) {
            this.game.zoom=shouldZoom;
        } else {
            this.game.zoom+=(shouldZoom-this.game.zoom)/20f;
        }
    }
}
