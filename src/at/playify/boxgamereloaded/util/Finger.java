package at.playify.boxgamereloaded.util;

import at.playify.boxgamereloaded.util.bound.PointBound;

//Finger/Cursor
public class Finger extends PointBound {

    @SuppressWarnings("WeakerAccess")
    public final int index;//Finger index
    public boolean down;//gedrückt
    public boolean control;//gedrückt auf einem Knopf/Menü

    public Finger(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Finger(index:"+index+",pressed:"+down+")";
    }
}
