package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.TickThread;
import at.playify.boxgamereloaded.block.Blocks;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.util.Finger;

public abstract class Game {
    protected boolean canDraw = false;
    public boolean[] keys = new boolean[300];
    public Finger[] fingers = new Finger[]{new Finger(0),new Finger(1),new Finger(2),new Finger(3),new Finger(4)};
    public VariableContainer vars=new VariableContainer();
    public Lock pauseLock=new Lock();
    public TickThread ticker;
    public Drawer d;
    public Level level;
    public Blocks blocks;
    public boolean paused;

    public Game(){
        init();
    }

    protected abstract void init();

    public void setCanDraw(boolean b) {
        canDraw = b;
    }

    public void setKey(int keyChar, boolean b) {
        keyChar=Character.toLowerCase(keyChar);
        if (keyChar < 300) {
            if (keys[keyChar]!=b) {
                keys[keyChar] = b;
                keyStateChanged(keyChar);
            }
        }
    }

    protected abstract void keyStateChanged(int keyCode);


    public abstract void fingerStateChanged(Finger finger);


    public void pause(){
        paused=true;
        pauseLock.lock();
    }
    public void resume(){
        pauseLock.unlock();
    }

    public abstract void tick();

    public abstract void draw();
    public void start(){
        if (ticker.getState()==Thread.State.NEW) {
            ticker.start();
        }
    }

    public static void report(Exception e) {
        e.printStackTrace();
    }
}