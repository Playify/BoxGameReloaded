package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.block.BlockAir;
import at.playify.boxgamereloaded.block.BlockOneWay;
import at.playify.boxgamereloaded.block.BlockSpike;
import at.playify.boxgamereloaded.level.compress.LevelData;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.util.bound.RectBound;

import java.util.ArrayList;

public class ServerLevel {
    public int sizeX;
    public int sizeY;
    public char[] blocks;
    public int[] metas;
    public final RectBound spawnPoint=new RectBound(.1f, 0, .8f, .8f);
    protected Server server;

    public ServerLevel(Server server) {
        this.server=server;
        blocks=new char[sizeX*sizeY];
        metas=new int[sizeX*sizeY];
    }

    public char get(int x, int y){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            char block = blocks[y * sizeX + x];
            return block!=0?block: BlockAir.chr;
        }else{
            return BlockAir.chr;
        }
    }


    public void set(int x, int y, char b) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            char block = blocks[y * sizeX + x];
            if (b==0)b=BlockAir.chr;
            if(block!=(blocks[y*sizeX+x]=b)) {
                metas[y * sizeX + x] = 0;
            }
        }
    }

    public void set(int x, int y, char b, int meta) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            if (b==0) {
                b=BlockAir.chr;
            }
            blocks[y * sizeX + x]=b;
            metas[y*sizeX+x]=meta%metaStates(b);
        }
    }

    public void setSize(int x, int y) {
        if(x>=0&&y>=0) {
            sizeX=x;
            sizeY=y;
            blocks=new char[sizeX*sizeY];
            metas=new int[sizeX*sizeY];
        }
    }

    public String toWorldString() {
        return server.compresser.compress(new LevelData(blocks,metas,spawnPoint,sizeX,sizeY));
    }
    public void loadWorldString(String s) {
        LevelData levelData=server.compresser.decompress(s);
        if (levelData!=null) {
            setSize(levelData.sizeX,levelData.sizeY);
            spawnPoint.set(levelData.spawnPoint);
            int index=0;
            while (levelData.hasNext()){
                levelData.next();
                blocks[index]=levelData.chr();
                metas[index]=levelData.meta();
                index++;
            }
        }
    }



    private int metaStates(char block) {
        if (block== BlockOneWay.chr||block== BlockSpike.chr) {
            return 4;
        }
        return 1;
    }
}
