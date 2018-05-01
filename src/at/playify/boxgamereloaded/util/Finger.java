package at.playify.boxgamereloaded.util;

import at.playify.boxgamereloaded.BoxGameReloaded;

//Finger/Cursor
public class Finger {
    public int index;//Finger index
    public boolean down;//gedrückt
    public boolean control;//gedrückt auf einem Knopf/Menü
    public float dx, dy;
    private float x;
    private float y;

    public Finger(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Finger(index:"+index+",pressed:"+down+")";
    }

    public void set(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x=x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y=y;
    }

    public void setDown(int x, int y, BoxGameReloaded game) {
        dx=(x-game.d.getWidth()/2)*game.vars.display_size*game.aspectratio/(game.d.getWidth()*game.zoom)+game.zoom_x;
        dy=-(y-game.d.getHeight()/2)*game.vars.display_size/(game.d.getHeight()*game.zoom)+game.zoom_y;
    }
}
