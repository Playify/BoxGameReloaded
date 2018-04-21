package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.paint.Paintable;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

//Blöcke müssen bei Blocks registriert werden.
public abstract class Block implements Paintable {
    final RectBound bound = new RectBound(0, 0, 1, 1);//Standard Bound, dies wird von Blöcken benutzt zur Kollision
    protected BoxGameReloaded game;//Referenz zum Spiel in dem es Registriert ist
    private final char chr;//Zeichen um es zu einem Text zu machen wenn das Level als Text versandt wird.


    //Spiel referenz und Zeichen für Level zu Text transformation
    public Block(BoxGameReloaded game,char c){
        this.chr=c;
        this.game=game;
        game.blocks.blockscount++;
        if (game.blocks.map[chr]!=null) {
            System.err.println("Overriding Block "+game.blocks.map[chr]+" (chr="+chr+") with "+this);
        }
        game.blocks.map[chr]=this;
        game.blocks.list.add(this);
    }

    //Kollision detecten
    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        return b.collide(bound.set(x,y));
    }

    //Zeichne Block bei den Koordinaten x,y
    public void draw(int x, int y, Level level) {
        game.d.rect(x,y,.5f,.5f,0xFFFF00FF);
        game.d.rect(x+.5f,y+.5f,.5f,.5f,0xFFFF00FF);
        game.d.rect(x,y,1,1,0xFF000000);
    }

    //Anzahl Metadatenzustände z.B. rotation
    public int metaStates(){
        return 1;
    }

    //Wenn der Block als Solide
    public boolean isSolid() {
        return true;
    }

    //Zeichen für Level zu Text bekommen
    public final char getChar() {
        return chr;
    }


    //KollisionsBoxen vom Block für die angegebenen Koordinaten zur Liste hinzufügen
    public abstract void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player); /*{
        list.add(Borrow.bound(x,y,x+1,y+1));
    }*/

    @Override
    public void draw(int meta) {
        game.d.pushMatrix();
        if (isBackGround(meta)) {
            game.d.translate(0, 0, -.4f);
        } else {
            game.d.translate(.5f, .5f, .5f);
            final int v=50;
            float angle=System.currentTimeMillis()%(360*v)/(float) v;
            game.d.rotate(angle, 0, 1, 0);
            game.d.translate(-.5f, -.5f, -.5f);
        }
        draw(0, 0, game.painter.fakeLevel);
        game.d.popMatrix();
    }

    @Override
    public String name(int data) {
        String name=getClass().getSimpleName();
        if (name.startsWith("Block")) {
            name=name.substring(5);
        }
        return name;
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
        int bx=(int) x, by=(int) y;
        if (game.level.get(bx, by)==this&&click) {
            game.level.setMeta(bx, by, game.level.getMeta(bx, by)+1);
        } else {
            game.level.set(bx, by, this);
        }
    }

    protected abstract boolean isBackGround(int meta);
}
