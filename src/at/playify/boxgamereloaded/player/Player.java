package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Borrow;
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
    public String skin = "cube";
    public boolean tail;
    private Borrow.BorrowedBoundingBox3d[] tailArray = new Borrow.BorrowedBoundingBox3d[25];
    private int tailindex;

    public void draw() {
        if (skin.equals("cube")) {
            if (game.vars.cubic) {
                float d = (bound.w() + bound.h()) / 2;
                game.d.cube(bound.x(), bound.y(), .5f - d / 2, bound.w(), bound.h(), d, color);
            }else{
                game.d.rect(bound.x(), bound.y(), bound.w(), bound.h(),color);
            }
        } else if (skin.equals("border")) {
            if (game.vars.cubic) {
                float d = (bound.w() + bound.h()) / 2;
                game.d.lineCube(bound.x(), bound.y(), .5f - d / 2, bound.w(), bound.h(), d, color);
            } else {
                game.d.lineRect(bound.x(), bound.y(), bound.w(), bound.h(), color);
            }
        } else if (skin.equals("bordercube")) {
            if (game.vars.cubic) {
                float d = (bound.w() + bound.h()) / 2;
                game.d.lineCube(bound.x(), bound.y(), .5f - d / 2, bound.w(), bound.h(), d, 0xFF000000);
            } else {
                game.d.lineRect(bound.x(), bound.y(), bound.w(), bound.h(), 0xFF000000);
            }
            if (game.vars.cubic) {
                float d = (bound.w() + bound.h()) / 2;
                game.d.cube(bound.x(), bound.y(), .5f - d / 2, bound.w(), bound.h(), d, color);
            } else {
                game.d.rect(bound.x(), bound.y(), bound.w(), bound.h(), color);
            }
        }
        if (tail) {
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound == null) {
                    break;
                } else {
                    if (skin.equals("cube")) {
                        if (game.vars.cubic) {
                            game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX - bound.minX, bound.maxY - bound.minY, bound.maxZ - bound.minZ, color);
                        } else {
                            game.d.rect(bound.minX, bound.minY, bound.maxX - bound.minX, bound.maxY - bound.minY, color);
                        }
                    } else if (skin.equals("border")) {
                        if (game.vars.cubic) {
                            game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX - bound.minX, bound.maxY - bound.minY, bound.maxZ - bound.minZ, color);
                        } else {
                            game.d.lineRect(bound.minX, bound.minY, bound.maxX - bound.minX, bound.maxY - bound.minY, color);
                        }
                    } else if (skin.equals("bordercube")) {
                        if (game.vars.cubic) {
                            game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX - bound.minX, bound.maxY - bound.minY, bound.maxZ - bound.minZ, 0xFF000000);
                        } else {
                            game.d.lineRect(bound.minX, bound.minY, bound.maxX - bound.minX, bound.maxY - bound.minY, 0xFF000000);
                        }
                        if (game.vars.cubic) {
                            game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX - bound.minX, bound.maxY - bound.minY, bound.maxZ - bound.minZ, color);
                        } else {
                            game.d.rect(bound.minX, bound.minY, bound.maxX - bound.minX, bound.maxY - bound.minY, color);
                        }
                    }
                }
            }
        }

        /*else if(skin.equals("glib")){
            game.d.startPath(glib0x,glib0y);
            game.d.addToPath(glib1x,glib1y);
            game.d.addToPath(glib2x,glib2y);
            game.d.finishPath(glib3x,glib3y,color);
        }*/
    }

    //tick für Spieler ausführen um Animationen auszuführen
    public void tick(){
        if (tail) {
            float w = (bound.w() / tailArray.length) / 2;
            float h = (bound.h() / tailArray.length) / 2;
            float d = (((bound.w() + bound.h()) / 2) / tailArray.length) / 2;
            for (Borrow.BorrowedBoundingBox3d borrowedBoundingBox3d : tailArray) {
                if (borrowedBoundingBox3d == null) {
                    break;
                }
                borrowedBoundingBox3d.minX += w;
                borrowedBoundingBox3d.minY += h;
                borrowedBoundingBox3d.minZ += d;
                borrowedBoundingBox3d.maxX -= w;
                borrowedBoundingBox3d.maxY -= h;
                borrowedBoundingBox3d.maxZ -= d;
            }
            int i = (tailindex++) % tailArray.length;
            if (tailArray[i] == null) {
                tailArray[i] = Borrow.bound3d();
            }
            float v = ((bound.w() + bound.h()) / 2) / 2;
            tailArray[i].set(bound.x(), bound.y(), .5f - v, bound.xw(), bound.yh(), .5f + v);
        } else {
            for (int i = 0; i < tailArray.length; i++) {
                if (tailArray[i] != null) {
                    tailArray[i].free();
                }
                tailArray[i] = null;
            }
        }




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
    final RectBound collider = new RectBound();
    int side() {
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
