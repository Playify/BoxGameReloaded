package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.paint.Paintable;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.bound.RectBound;

//Basis für Spieler
public abstract class Player implements Paintable {
    protected final BoxGameReloaded game;
    //Derzeitige Geschwindigkeit
    public float motionX;
    public float motionY;

    public Player(BoxGameReloaded game){
        this.game = game;
    }

    public final RectBound bound = new RectBound(0, 0, 0.8f, 0.8f);
    public final BoundingBox3d drawbound=new BoundingBox3d(0, 0, 0, 0, 0, 0);
    private int color=0xFF00FF00;
    public String skin = "cube";
    public boolean tail;
    private Borrow.BorrowedBoundingBox3d[] tailArray = new Borrow.BorrowedBoundingBox3d[25];
    private int tailindex;

    public void draw() {
        float d=(bound.w()+bound.h())/4;
        drawbound.set(bound.x(), bound.y(), -d+.5f, bound.xw(), bound.yh(), d+.5f);
        drawPart(drawbound);
        if (tail) {
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound == null) {
                    break;
                } else {
                    drawPart(bound);
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

    @Override
    public void draw(int data) {
        game.d.pushMatrix();
        game.d.translate(.5f, .5f, .5f);
        final int v=50;
        float angle=System.currentTimeMillis()%(360*v)/(float) v;
        game.d.rotate(angle, 0, 1, 0);
        game.d.translate(-.5f, -.5f, -.5f);
        drawbound.set(0, 0, 0, 1, 1, 1);
        drawPart(drawbound);
        game.d.popMatrix();
    }


    private void drawPart(BoundingBox3d bound) {
        if (skin.equals("cube")) {
            if (game.vars.cubic) {
                game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        } else if (skin.equals("border")) {
            if (game.vars.cubic) {
                game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        } else if (skin.equals("bordercube")) {
            if (game.vars.cubic) {
                game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, 0xFF000000);
            } else {
                game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, 0xFF000000);
            }
            if (game.vars.cubic) {
                game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
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
                    tailArray[i]=null;
                }
            }
        }
    }


    //Spielername
    public abstract String name();
}
