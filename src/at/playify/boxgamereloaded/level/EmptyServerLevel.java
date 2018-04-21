package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.block.BlockAir;

public class EmptyServerLevel extends ServerLevel {
    public EmptyServerLevel(){
        super.setSize(25, 25);
        super.set(0,0,'a',0);
    }

    public char get(int x, int y){
        return BlockAir.chr;
    }

    public void set(int x, int y, char b) {
    }

    public void set(int x, int y, char b, int meta) {
    }

    public void setSize(int x, int y) {
        if(x>=0&&y>=0) {
            sizeX=x;
            sizeY=y;
            blocks=new char[sizeX*sizeY];
            metas=new int[sizeX*sizeY];
        }
    }

    @Override
    public void loadWorldString(String s) {

    }
}
