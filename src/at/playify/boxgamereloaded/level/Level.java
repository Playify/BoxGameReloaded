package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.level.compress.LevelData;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.paint.PlayPaint;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class Level {
    public int sizeX,sizeY;//Levelgröße nur setzen mit der Methode setSize(x,y)
    protected BoxGameReloaded game;
    private Block[] blocks;//Blöcke im Level
    private int[] metas;//Metadaten der Blöcke
    public final RectBound spawnPoint=new RectBound(.1f, 0f, .8f, .8f);
    private ArrayList<String> history=new ArrayList<>();
    private boolean dirty;
    private String worldstring;

    public Level(BoxGameReloaded game) {
        this.game = game;
        blocks=new Block[sizeX*sizeY];
        metas=new int[sizeX*sizeY];
        markDirty();
    }

    //Block auf position bekommen, wenn out of map dann Luft
    public Block get(int x, int y){
        return get(x,y,game.blocks.AIR);
    }

    //Block auf position bekommen, wenn out of map dann def
    public Block get(int x, int y,Block def){
        try {
            if ((x|y) >= 0&&x<sizeX&&y<sizeY) {
                Block block=blocks[y*sizeX+x];
                return block!=null ? block : game.blocks.AIR;
            } else if ((game.vars.geometry_dash||game.gui.isMainMenuVisible())&&y >= 0&&y<sizeY) {
                if (x==sizeX+6) return game.blocks.FINISH;
                return game.blocks.AIR;
            } else if (game.painter.draw&&!(game.painter.paint() instanceof PlayPaint)) {
                if (x==-2||x==sizeX+1) {
                    int yy=y-(int) (game.zoom_y);
                    if (game.zoom_y==sizeY&&sizeY!=0) yy++;
                    if (-1<=yy&&yy<(sizeX==0||sizeY==0 ? 1 : 2)) return game.blocks.ICON;
                }
                if (y==-2||y==sizeY+1) {
                    int xx=x-(int) (game.zoom_x);
                    if (game.zoom_x==sizeX&&sizeX!=0) xx++;
                    if (-1<=xx&&xx<(sizeX==0||sizeY==0 ? 1 : 2)) return game.blocks.ICON;
                }
                return def;
            } else {
                return def;
            }
        }catch (ArrayIndexOutOfBoundsException e){
            return game.blocks.AIR;
        }
    }

    //Metadata auf position bekommen
    public int getMeta(int x, int y){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            return metas[y*sizeX+x];
        }else if (game.painter.draw) {
            int meta=0;
            if (x==-2||x==sizeX+1) {
                int yy=y-(int)(game.zoom_y);
                if (game.zoom_y==sizeY&&sizeY!=0)yy++;
                if (-1<=yy&&yy<(sizeX==0||sizeY==0?1:2))meta=yy+1;
            }
            if (y==-2||y==sizeY+1) {
                int xx=x-(int)(game.zoom_x);
                if (game.zoom_x==sizeX&&sizeX!=0)xx++;
                if (-1<=xx&&xx<(sizeX==0||sizeY==0?1:2))meta=xx+1;
            }
            if (sizeX==0||sizeY==0)meta*=2;
            return meta;
        }else{
            return 0;
        }
    }

    private final RectBound rect=new RectBound();

    //Metadata auf position setzen
    public void setMeta(int x, int y, int meta) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            Block block = blocks[y * sizeX + x];
            if (block==null) {
                block=game.blocks.AIR;
            }
            metas[y*sizeX+x]=meta% block.metaStates();
            markDirty();
        }
    }

    //Setze Block auf Koordinaten
    public void set(int x, int y, Block b) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            Block block = blocks[y * sizeX + x];
            if (b==null)b=game.blocks.AIR;
            if(block!=(blocks[y*sizeX+x]=b)) {
                metas[y * sizeX + x] = 0;
            }
            markDirty();
        }
    }

    //Setze Block und Metadaten auf Koordinaten
    public void set(int x, int y, Block b, int meta) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            if (b==null) {
                b=game.blocks.AIR;
            }
            blocks[y * sizeX + x]=b;
            metas[y*sizeX+x]=meta%b.metaStates();
            markDirty();
        }
    }

    //Kollision checken
    public boolean collide(Bound b) {
        if (game.vars.noclip) return false;//if isplayer
        Block bl;
        rect.sizeOf(b);
        float cx = b.cx();
        float cy = b.cy();
        for (int x=(int) (-1 - rect.w()/ 2); x <= 1 + rect.w()/ 2; x++)
            for (int y=(int) (-1 - rect.h()/ 2); y <= 1 + rect.h()/ 2; y++) {
                int xx = (int) (x + cx);
                int yy = (int) (y + cy);
                bl = get(xx, yy);
                if (bl.collide(b, xx, yy, false, getMeta(xx, yy), this))
                    return true;
            }

        return false;
    }

    //Alle Blöcke mit denen kollidiert wird als Array bekommen
    public ArrayList<Borrow.BorrowedCollisionData> collideList(Bound b, ArrayList<Borrow.BorrowedCollisionData> lst) {
        Block bl;
        rect.sizeOf(b);
        float cx = b.cx();
        float cy = b.cy();
        for (int x=(int) (-1 - rect.w()/ 2); x <= 1 + rect.w()/ 2; x++) {
            for (int y = (int) (-1 - rect.h() / 2); y <= 1 + rect.h() / 2; y++) {
                int xx = (int) (x + cx);
                int yy = (int) (y + cy);
                bl = get(xx, yy);
                int meta = getMeta(xx, yy);
                if (bl.collide(b, xx, yy, true, meta, this)) {
                    lst.add(Borrow.data(bl, xx, yy, meta));
                }
            }
        }
        return lst;
    }

    //Größe festlegen
    public void setSize(int x, int y) {
        if(x>=0&&y>=0) {
            sizeX=x;
            sizeY=y;
            blocks=new Block[sizeX*sizeY];
            metas=new int[sizeX*sizeY];
            markDirty();
        }
    }

    //Level zeichnen
    public void draw(RectBound b) {
        if (game.painter.draw) {
            float d=(spawnPoint.w()+spawnPoint.h())/2;
            if (game.vars.cubic) {
                game.d.lineCube(spawnPoint.x(), spawnPoint.y(), .5f-d/2, spawnPoint.w(), spawnPoint.h(), d, 0xFFFF0000);
            } else {
                game.d.lineRect(spawnPoint.x(), spawnPoint.y(), spawnPoint.w(), spawnPoint.h(), 0xFFFF0000);
            }
        }
        RectBound bound=game.vars.check.bound;
        if (!(game.gui.isMainMenuVisible()&&bound.equals(spawnPoint))) {
            float d=(bound.w()+bound.h())/2;
            if (game.vars.cubic) {
                game.d.lineCube(bound.x(), bound.y(), .5f-d/2, bound.w(), bound.h(), d, 0xFF000000);
            } else {
                game.d.lineRect(bound.x(), bound.y(), bound.w(), bound.h(), 0xFF000000);
            }
        }
        game.player.draw();
        if (game.connection!=null&&!game.gui.isMainMenuVisible()) {
            Player[] players=game.connection.players;
            for (Player player : players) {
                player.draw();
            }
        }
        int minX = (int) Math.floor(b.x()), minY = (int) Math.floor(b.y()), maxX = (int) Math.ceil(b.x()+b.w()), maxY = (int) Math.ceil(b.y()+b.h());
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                if (game.painter.draw&&game.vars.paintPoints&&x>0&&y>0&&x<sizeX&&y<sizeY)
                    game.d.point(x, y, 0, 0xFF000000);
                Block block= get(x, y);
                block.draw(x,y,this);
            }
        }
        if (game.vars.debug.blockdata) {
            for (int y=minY;y<maxY;y++) {
                for (int x=minX;x<maxX;x++) {
                    int meta=getMeta(x, y);
                    Block block=get(x, y, game.blocks.GROUND);
                    game.d.drawStringCenter(block.metaStates()==1&&meta==0? block.getChar()+"": block.getChar()+""+meta, x+.5f, y+0.25f, .5f, 0x66010AFA);
                }
            }
        }
    }

    //Level zu Text
    public String toWorldString() {
        if (dirty||worldstring==null) {
            dirty=false;
            return worldstring=game.compresser.compress(new LevelData(blocks,metas,spawnPoint,sizeX,sizeY));
        }else{
            return worldstring;
        }
    }

    //Level aus Text laden
    public void loadWorldString(String s) {
        LevelData levelData=game.compresser.decompress(s);
        if (levelData!=null) {
            setSize(levelData.sizeX,levelData.sizeY);
            spawnPoint.set(levelData.spawnPoint);
            int index=0;
            while (levelData.hasNext()){
                levelData.next();
                blocks[index]=game.blocks.get(levelData.chr());
                metas[index]=levelData.meta();
                index++;
            }
        }
        game.zoom_x=Utils.clamp(game.zoom_x, 0, game.level.sizeX);
        game.zoom_y=Utils.clamp(game.zoom_y, 0, game.level.sizeY);
        markDirty();
    }

    //Kollisionsboxen vom Level erhalten
    public ArrayList<Borrow.BorrowedBoundingBox> getCollisionBoxes(PlayerSP player, Borrow.BorrowedBoundingBox bound) {
        ArrayList<Borrow.BorrowedBoundingBox> list = Borrow.boundList();
        int minX = (int) Math.floor(bound.minX) - 1;
        int maxX = (int) Math.ceil(bound.maxX) + 1;
        int minY = (int) Math.floor(bound.minY) - 1;
        int maxY = (int) Math.ceil(bound.maxY) + 1;
        int coordX;
        int coordY;

        for (int k1 = minX; k1 < maxX; ++k1) {
            for (int l1 = minY; l1 < maxY; ++l1) {
                boolean flag2 = k1 == minX || k1 == maxX - 1;
                boolean flag3 = l1 == minY || l1 == maxY - 1;

                if ((!flag2 || !flag3)) {
                    coordX=k1;
                    coordY=l1;
                    Block block;

                    block=get(coordX,coordY,game.blocks.GROUND);

                    block.getCollisionBox(this, coordX,coordY, bound, list, player);
                }
            }
        }
        return list;
    }

    public Borrow.BorrowedCollisionData findNext(Borrow.BorrowedCollisionData col) {
        int index=col.y*sizeX+col.x+1;
        int size=sizeX*sizeY;
        for (int i=0;i<size;i++) {
            int pos=(index+i)%size;
            if (blocks[pos]==col.blk&&metas[pos]==col.meta) {
                return Borrow.data(col.blk, pos%sizeX, pos/sizeX, col.meta);
            }
        }
        return Borrow.data(col.blk, col.x, col.y, col.meta);
    }

    public void saveHistory() {
        String s=toWorldString();
        if (history.isEmpty()||!history.get(0).equals(s))
            history.add(0, s);
    }
    public void clearHistory() {
        history.clear();
    }

    public void loadHistory(){
        if (!history.isEmpty()) {
            loadWorldString(history.remove(0));
            game.connection.sendPacketSoon(new PacketLevelData(toWorldString()));
        }
    }
    public boolean hasHistory(){
        if (history.isEmpty()) {
            return false;
        }
        if (toWorldString().equals(history.get(0))) {
            return false;
        }
        return true;
    }

    public void markDirty() {
        dirty=true;
    }


    public void shift(int x, int y) {
        Level level=this;
        x=level.sizeX==0?0:((x%level.sizeX)+level.sizeX)%level.sizeX;
        y=level.sizeY==0?0:((y%level.sizeY)+level.sizeY)%level.sizeY;
        if (x==0&&y==0) {
            return;
        }
        Block[] blocks=level.blocks;
        int[] metas=level.metas;
        Block[] blk=new Block[blocks.length];
        int[] mta=new int[metas.length];
        for (int i=0;i<blocks.length;i++) {
            int ii=(i+x)%(blocks.length);
            if (x!=0&&ii%level.sizeX<x) {
                ii+=blocks.length-level.sizeX;
            }
            ii+=y*level.sizeX;
            blk[i]=blocks[ii%blocks.length];
            mta[i]=metas[ii%blocks.length];
        }
        level.blocks=blk;
        level.metas=mta;
        level.spawnPoint.shift(-x,-y,level.sizeX,level.sizeY);
        game.player.bound.shift(-x,-y,level.sizeX,level.sizeY);
        game.vars.check.shift(-x,-y,level.sizeX,level.sizeY);
        level.markDirty();
    }

    public void size(int x, int y) {
        Level level=this;
        if (x==0&&y==0) {
            return;
        }
        if (x<0)x=0;
        if (y<0)y=0;
        Block[] blocks=level.blocks;
        int[] metas=level.metas;
        Block[] blk=new Block[x*y];
        int[] mta=new int[x*y];
        int yy=Math.min(y, level.sizeY);
        int xx=Math.min(x, level.sizeX);
        for (int i=0;i<yy;i++) {
            System.arraycopy(blocks,level.sizeX*i,blk,x*i,xx);
            System.arraycopy(metas,level.sizeX*i,mta,x*i,xx);
        }
        level.sizeX=x;
        level.sizeY=y;
        level.blocks=blk;
        level.metas=mta;
        level.markDirty();
    }
}
