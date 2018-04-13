package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.bound.RectBound;

//Basis für Spieler
public abstract class Player {
    protected final BoxGameReloaded game;
    //Derzeitige Geschwindigkeit
    public float motionX;
    public float motionY;
    private float glib0x,glib0y,glib1x,glib1y,glib2x,glib2y,glib3x,glib3y;

    public Player(BoxGameReloaded game){
        this.game = game;
    }

    public final RectBound bound = new RectBound(0, 0, 0.8f, 0.8f);
    private int color=0xFF00FF00;
    private String skin="cube";

    public void draw() {
        if (skin.equals("cube")) {
            if (game.vars.cubic) {
                game.d.cube(bound.x(), bound.y(), .1f, bound.w(), bound.h(), (bound.w()+bound.h()) / 2, color);
            }else{
                game.d.rect(bound.x(), bound.y(), bound.w(), bound.h(),color);
            }
        }else if(skin.equals("glib")){/*
            game.d.startPath(glib0x,glib0y);
            game.d.addToPath(glib1x,glib1y);
            game.d.addToPath(glib2x,glib2y);
            game.d.finishPath(glib3x,glib3y,color);*/
        }
    }

    //tick für Spieler ausführen um Animationen auszuführen
    public void tick(){
        int side=side();
        float cx = bound.cx();
        float cy = bound.cy();
        float motionY=this.motionY;
        float motionX=this.motionX;
        if(side!=0&&motionY>0.05f){
            motionY=0.05f;
        }
        if(side!=0&&motionY<-0.05f){
            motionY=-0.05f;
        }
        if (side!=0) {
            motionX = 0;
        }

        float top = Math.max(Math.min(bound.w(), bound.w()+ bound.w()* motionY), bound.w()/ 2) / 2;
        float bottom = Math.max(Math.min(bound.w(), (bound.w()- bound.w()* motionY)), bound.w()/ 2) / 2;


        glib0x = cx - ((side & 2) != 0 ? 0.4f : top) + motionX;
        glib1x = cx + ((side & 1) != 0 ? 0.4f : top) + motionX;
        glib2x = cx + ((side & 2) != 0 ? 0.4f : bottom);
        glib3x = cx - ((side & 1) != 0 ? 0.4f : bottom);
        float r1 = -((float) Math.sin((game.ticker.ticks) / 7f) *bound.h()/ 15 - bound.h()/ 15);
        float r2 = -((float) Math.sin((game.ticker.ticks) / 7f + 0.1) *bound.h()/ 15 - bound.h()/ 15);
        glib0y = cy - bound.h()/ 2 - motionX / 2 + ((side & 1) != 0 ? motionY * 2 : side == 0 ? r1 : 0);
        glib1y = cy - bound.h()/ 2 + motionX / 2 + ((side & 2) != 0 ? motionY * 2 : side == 0 ? r2 : 0);
        glib2y = cy + bound.h()/ 2 + motionX * motionY * 2 - ((side & 1) != 0 ? motionY * 2 : 0);
        glib3y = cy + bound.h()/ 2 - motionX * motionY * 2 - ((side & 2) != 0 ? motionY * 2 : 0);
    }

    //Kollisionsseite, wenn überhaupt. Benutzt zum wallsliden
    protected final RectBound collider = new RectBound();
    protected int side() {
        if (!game.vars.wallslide) {
            return 0;
        }
        int side=0;
        if (game.level.collide(collider.set(bound).move(-0.1f,0),this)) {
            side|=2;
        }
        if (game.level.collide(collider.set(bound).move(0.1f,0),this)) {
            side|=1;
        }
        return side;
    }

    //Spielername
    public abstract String name();
}
