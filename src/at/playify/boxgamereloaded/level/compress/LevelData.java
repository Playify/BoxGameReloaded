package at.playify.boxgamereloaded.level.compress;

import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.block.BlockAir;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class LevelData {
    public RectBound spawnPoint=new RectBound();
    public int sizeX;
    public int sizeY;
    private char[] chars;
    private int[] metas;
    private int index=-1;

    public LevelData(char[] blocks, int[] metas, RectBound spawnPoint, int sizeX, int sizeY) {
        this.chars=blocks;
        this.metas=metas;
        this.spawnPoint.set(spawnPoint);
        this.sizeX=sizeX;
        this.sizeY=sizeY;
    }

    public LevelData(Block[] blocks, int[] metas, RectBound spawnPoint, int sizeX, int sizeY) {
        this.chars=new char[blocks.length];
        for (int i=0;i<blocks.length;i++) {
            chars[i]=blocks[i]==null ? BlockAir.chr : blocks[i].getChar();
        }
        this.metas=metas;
        this.spawnPoint.set(spawnPoint);
        this.sizeX=sizeX;
        this.sizeY=sizeY;
    }

    public boolean hasNext() {
        return index+1<sizeX*sizeY;
    }

    public void next() {
        index++;
    }

    public char chr() {
        char c=chars[index];
        return c==0?BlockAir.chr:c;
    }

    public int meta() {
        return metas[index];
    }
}
