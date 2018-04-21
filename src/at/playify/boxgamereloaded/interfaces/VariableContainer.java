package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class VariableContainer {
    public float tickrate=60;
    public float display_size=10;
    public float zoom_level=1.3f;
    public int jumps=2;
    public boolean inverted_gravity;
    public boolean geometry_dash;
    public boolean fly;
    public boolean noclip;
    public boolean god;
    public boolean cubic=true;
    public boolean cubic_check=true;
    public int deaths=0;
    public Debug debug=new Debug();
    public Checkpoint check=new Checkpoint();
    public String playername="TheUser"+System.currentTimeMillis();
    public final boolean scrollPaint=false;
    public boolean tickOnDraw;
    public boolean instant_zoom;
    public String world="NONE";
    public boolean paintPoints;
    private BoxGameReloaded game;

    public VariableContainer(Game game) {
        if (game instanceof BoxGameReloaded) {
            this.game=(BoxGameReloaded) game;
        }
    }

    public class Debug {
        public boolean viewback;
        public boolean drawback;//Rückseite von Würfeln zeichnen
        public boolean console;
    }

    public class Checkpoint {
        private int jumps;
        private boolean geometry_dash;
        private boolean inverted_gravity;
        private float motionX;
        private float motionY;
        public RectBound bound=new RectBound();

        public void check() {
            check(game.player.bound);
        }

        public void die() {
            VariableContainer vars=VariableContainer.this;
            vars.geometry_dash=geometry_dash;
            vars.inverted_gravity=inverted_gravity;
            game.player.jumps=jumps;
            game.player.motionX=motionX;
            game.player.motionY=motionY;
            game.player.bound.set(bound);
        }

        public void check(RectBound spawnPoint) {
            VariableContainer vars=VariableContainer.this;
            geometry_dash=vars.geometry_dash;
            inverted_gravity=vars.inverted_gravity;
            jumps=game.player.jumps;
            motionX=game.player.motionX;
            motionY=game.player.motionY;
            bound.set(spawnPoint);
        }
    }
}
