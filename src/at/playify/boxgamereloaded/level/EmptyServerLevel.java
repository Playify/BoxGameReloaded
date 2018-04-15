package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.block.BlockAir;

public class EmptyServerLevel extends ServerLevel {
    public EmptyServerLevel(){
        super.setSize(5,5);
        super.set(0,0,'a',0);
    }

    public char get(int x, int y){
        return BlockAir.chr;
    }

    public boolean set(int x, int y, char b) {
        return x==0&&y==0;
    }
    public boolean set(int x, int y, char b,int meta) {
        return x==0&&y==0;
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
