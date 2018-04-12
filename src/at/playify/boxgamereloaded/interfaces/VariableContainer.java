package at.playify.boxgamereloaded.interfaces;

public class VariableContainer {
    public static int STATE_SETTINGS=0b100,STATE_MAINMENU=0b10,STATE_PAUSE=0b1;
    public float tickrate=60;
    public int state=STATE_MAINMENU;
    public float display_size=10;
    public float zoom_level=1.3f;
    public int jumps=2;
    public boolean wallbounce=false;
    public boolean wallslide=false;
    public boolean fly;
    public boolean noclip;
    public boolean god;
    public boolean cubic=true;
    public boolean cubic_check=true;
    public int deaths=0;
    public Debug debug=new Debug();
    public String playername="TheUser";
    public String world;
    public boolean tickOnDraw;

    public class Debug {
        public boolean viewback;
    }
}
