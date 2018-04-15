package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.Compresser;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.Bound;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class Level {
    public int sizeX,sizeY;//Levelgröße nur setzen mit der Methode setSize(x,y)
    protected BoxGameReloaded game;
    private Block[] blocks;//Blöcke im Level
    private int[] metas;//Metadaten der Blöcke
    public RectBound spawnPoint=new RectBound(.1f,.1f,.8f,.8f);
    public Level(BoxGameReloaded game) {
        this.game = game;
        blocks=new Block[sizeX*sizeY];
        metas=new int[sizeX*sizeY];
    }

    //Block auf position bekommen, wenn out of map dann Luft
    public Block get(int x, int y){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            Block block = blocks[y * sizeX + x];
            return block!=null?block:game.blocks.AIR;
        } else if (game.vars.geometry_dash && y >= 0 && y < sizeY) {
            if (x >= sizeX + 6) return game.blocks.FINISH;
            return game.blocks.AIR;
        }else{
            return game.blocks.AIR;
        }
    }

    //Block auf position bekommen, wenn out of map dann def
    public Block get(int x, int y,Block def){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            Block block = blocks[y * sizeX + x];
            return block!=null?block:game.blocks.AIR;
        } else if (game.vars.geometry_dash && y >= 0 && y < sizeY) {
            if (x == sizeX + 6) return game.blocks.FINISH;
            return game.blocks.AIR;
        }else{
            return def;
        }
    }

    //Metadata auf position bekommen
    public int getMeta(int x, int y){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            return metas[y*sizeX+x];
        }else{
            return 0;
        }
    }
    //Metadata auf position setzen
    public boolean setMeta(int x, int y,int meta){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            Block block = blocks[y * sizeX + x];
            if (block==null) {
                block=game.blocks.AIR;
            }
            metas[y*sizeX+x]=meta% block.metaStates();
            return true;
        }else {
            return false;
        }
    }
    //Setze Block auf Koordinaten
    public boolean set(int x, int y, Block b) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            Block block = blocks[y * sizeX + x];
            if (b==null)b=game.blocks.AIR;
            if(block!=(blocks[y*sizeX+x]=b)) {
                metas[y * sizeX + x] = 0;
            }
            return true;
        }else{
            return false;
        }
    }
    //Setze Block und Metadaten auf Koordinaten
    public boolean set(int x, int y, Block b,int meta) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            if (b==null) {
                b=game.blocks.AIR;
            }
            blocks[y * sizeX + x]=b;
            metas[y*sizeX+x]=meta%b.metaStates();
            return true;
        }else{
            return false;
        }
    }

    private RectBound rect=new RectBound();

    //Kollision checken
    public boolean collide(Bound b, Player player) {
        if (game.vars.noclip) return false;//if isplayer
        Block bl;
        rect.sizeOf(b);
        float cx = b.cx();
        float cy = b.cy();
        for (int x=(int) (-1 - rect.w()/ 2); x <= 1 + rect.w()/ 2; x++)
            for (int y=(int) (-1 - rect.h()/ 2); y <= 1 + rect.h()/ 2; y++) {
                bl = get((int) (x + cx), (int) (y + cy));
                if (bl.collide(b, (int) (x + cx), (int) (y + cy), player, false, getMeta(x,y), this))
                    return true;
            }

        return false;
    }

    //Alle Blöcke mit denen kollidiert wird als Array bekommen
    public void collideList(Bound b, Player player, Block[] lst) {
        Block bl;
        rect.sizeOf(b);
        float cx = b.cx();
        float cy = b.cy();
        int index=0;
        for (int x=(int) (-1 - rect.w()/ 2); x <= 1 + rect.w()/ 2; x++) {
            for (int y = (int) (-1 - rect.h() / 2); y <= 1 + rect.h() / 2; y++) {
                bl = get((int) (x + cx), (int) (y + cy));
                if (bl.collide(b, (int) (x + cx), (int) (y + cy), player, true, getMeta(x, y), this)) {
                    boolean add = true;
                    for (int i = 0; i < index; i++) {
                        if (lst[i] == bl) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        lst[index++] = bl;
                    }
                }
            }
        }
    }

    //Größe festlegen
    public void setSize(int x, int y) {
        if(x>=0&&y>=0) {
            sizeX=x;
            sizeY=y;
            blocks=new Block[sizeX*sizeY];
            metas=new int[sizeX*sizeY];
        }
    }

    //Level zeichnen
    public void draw(RectBound b) {
        float d = (spawnPoint.w() + spawnPoint.h()) / 2;
        if (game.vars.cubic) {
            game.d.lineCube(spawnPoint.x(), spawnPoint.y(), .5f - d / 2, spawnPoint.w(), spawnPoint.h(), d, 0xFF000000);
        } else {
            game.d.lineRect(spawnPoint.x(), spawnPoint.y(), spawnPoint.w(), spawnPoint.h(), 0xFF000000);
        }
        int minX = (int) Math.floor(b.x()), minY = (int) Math.floor(b.y()), maxX = (int) Math.ceil(b.x()+b.w()), maxY = (int) Math.ceil(b.y()+b.h());
        game.player.draw();
        if (game.connection!=null) {
            Player[] players=game.connection.players;
            for (Player player : players) {
                player.draw();
            }
        }
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                Block block= get(x, y);
                block.draw(x,y,this);
            }
        }
    }

    //Level zu Text
    public String toWorldString() {
        StringBuilder str=new StringBuilder();
        for(int i=0; i<blocks.length; i++) {
            Block block=blocks[i];
            str.append(block==null?'a':block.getChar());
            int meta=metas[i];
            if (meta!=0) {
                str.append(meta);
            }
        }
        str.append("-").append(sizeX);
        str.append("-").append(sizeY);
        str.append("-").append(((int) spawnPoint.x() * 3100));
        str.append("-").append(((int) spawnPoint.y() * 3100));
        str.append("-").append(((int) spawnPoint.w() * 100));
        str.append("-").append(((int) spawnPoint.h() * 100));
        return Compresser.compress(str.toString());
    }

    //Level aus Text laden
    public void loadWorldString(String s) {
        s=Compresser.decompress(s);
        String[] split=new String[]{"","","","","","",""};
        StringBuilder stringBuilder = new StringBuilder();
        int index=0;
        for (char c : s.toCharArray()) {
            if (c == '+') {
                split[index++]=stringBuilder.toString();
                stringBuilder.setLength(0);
                if (index>split.length) {
                    return;
                }
            }else {
                stringBuilder.append(c);
            }
        }
        setSize(Utils.parseInt(split[1],32),Utils.parseInt(split[2],18));
        spawnPoint.set(Utils.parseInt(split[3],10)/100f,Utils.parseInt(split[4],10)/100f,Utils.parseInt(split[5],80)/100f,Utils.parseInt(split[6],80)/100f);
        spawnPoint.round();
        char[] chars=split[0].toCharArray();
        int number=0;
        int i=0;
        Block block=null;
        for(char c : chars) {
            if (c>='0'&&c<='9'){
                number=number*10+(c-'0');
            }else{
                if (block!=null){
                    blocks[i]=block;
                    metas[i]=number;
                    number=0;
                    i++;
                }
                block=game.blocks.get(c);
            }
        }
        if (block!=null){
            blocks[i]=block;
            metas[i]=number;
        }
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
}
