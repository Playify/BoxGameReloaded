package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.TickThread;
import at.playify.boxgamereloaded.block.Blocks;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Lock;

public abstract class Game {
    public static final String allowedChars = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    @SuppressWarnings("WeakerAccess")
    public final Handler handler;
    public final boolean[] keys=new boolean[300];
    public final Finger[] fingers=new Finger[]{new Finger(0), new Finger(1), new Finger(2), new Finger(3), new Finger(4)};
    public final Lock pauseLock=new Lock();
    private final StringBuilder str=new StringBuilder();
    public Drawer d;
    public Level level;
    public Blocks blocks;
    public boolean paused;

    public Game(Handler handler){
        this.handler=handler;
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

    protected TickThread ticker;

    public static void report(Exception e) {
        e.printStackTrace();
    }

    protected abstract void keyStateChanged(int keyCode);


    public abstract void fingerStateChanged(Finger finger);


    //genutzt von Android
    @SuppressWarnings("unused")
    public void pause(){
        paused=true;
    }
    //genutzt von Android
    @SuppressWarnings("unused")
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

    public void cheatCode(char c) {
        str.append(c);
        String konami="uuddlrlrbas";
        while (str.length() > konami.length()) {
            str.deleteCharAt(0);
        }
        String s = str.toString();
        if (s.endsWith(konami)) {
            ((BoxGameReloaded) this).vars.debug.console^=true;
            ((BoxGameReloaded) this).vars.loader.save();
            System.out.println("Console activated");
            str.setLength(0);
        }
    }

    public abstract void runcmd(String text);
}
