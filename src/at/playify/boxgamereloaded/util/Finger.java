package at.playify.boxgamereloaded.util;

//Finger/Cursor
public class Finger {
    public int index;//Finger index
    public boolean down;//gedrückt
    public boolean control;//gedrückt auf einem Knopf/Menü
    public float x;
    public float y;

    public Finger(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Finger(index:"+index+",pressed:"+down+")";
    }

    public void set(float x, float y) {
        this.x=x;
        this.y=y;
    }

}
