package at.playify.boxgamereloaded.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;

public class MovePaint implements Paintable {
    private BoxGameReloaded game;

    public MovePaint(BoxGameReloaded game) {
        this.game=game;
    }

    @Override
    public void draw(int data) {
        //TODO
    }

    @Override
    public String name(int data) {
        return "Play";
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
    }
}
