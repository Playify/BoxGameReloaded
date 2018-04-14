package at.playify.boxgamereloaded.interfaces;

public class VariableContainer {
    public float tickrate=60;
    public float display_size=10;
    public float zoom_level=1.3f;
    public int jumps=2;
    public boolean wallbounce;
    public boolean wallslide;
    public boolean inverted_gravity;
    public boolean fly;
    public boolean noclip;
    public boolean god;
    public boolean cubic=true;
    public boolean cubic_check=true;
    public int deaths=0;
    public Debug debug=new Debug();
    public String playername="TheUser"+System.currentTimeMillis();
    public String world;
    public boolean tickOnDraw;

    public class Debug {
        public boolean viewback;
        public boolean drawback;//Rückseite von Würfeln zeichnen
    }
}
