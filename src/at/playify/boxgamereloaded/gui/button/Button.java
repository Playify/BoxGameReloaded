package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public abstract class Button implements Comparable<Button> {
    protected final BoxGameReloaded game;
    protected final BoundingBox3d buttonBound=new BoundingBox3d(0, 0, 0, 0, 0, 0);
    private long time;

    public Button(BoxGameReloaded game) {
        this.game = game;
    }

    public abstract String genText();

    public int genColor() {
        if (time<System.currentTimeMillis()) return 0xFF004A60;
        else return 0xFF005C7A;
    }

    public abstract BoundingBox3d genBound();

    public void draw(Drawer d) {
        d.pushMatrix();
        BoundingBox3d bound=genBound();
        d.cube(0, 0, 0, 1, 1, 1, genColor());
        float v=(bound.maxY-bound.minY);
        float v2=(bound.maxX-bound.minX);
        d.scale(1/v2, 1/v,1);
        d.drawStringCenter(genText(), v2/2, v/4, v/2, 0x66000000);
        d.popMatrix();
    }

    @Override
    public int compareTo(Button o) {
        return Float.compare(genBound().minZ, o.genBound().minZ);
    }

    public abstract boolean click(Finger finger);

    //Rückgabewert bedeutet: Kann pausiert werden
    //d.h. während animation false zurückgeben
    public boolean tick() {
        return true;
    }

    public void onClick() {
        time=System.currentTimeMillis()+50;
    }
}
