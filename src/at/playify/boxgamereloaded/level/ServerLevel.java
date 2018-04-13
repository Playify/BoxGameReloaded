package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.block.BlockAir;
import at.playify.boxgamereloaded.block.BlockOneWay;
import at.playify.boxgamereloaded.block.BlockSpike;
import at.playify.boxgamereloaded.util.Compresser;
import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class ServerLevel {
    public int sizeX,sizeY;
    protected char[] blocks;
    protected int[] metas;
    public RectBound spawnPoint=new RectBound(.1f,.1f,.8f,.8f);
    public ServerLevel() {
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

    public int getMeta(int x, int y){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            return metas[y*sizeX+x];
        }else{
            return 0;
        }
    }
    public boolean setMeta(int x, int y,int meta){
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            char block = blocks[y * sizeX + x];
            if (block==0) {
                block=BlockAir.chr;
            }
            metas[y*sizeX+x]=meta% metaStates(block);
            return true;
        }else {
            return false;
        }
    }


    public boolean set(int x, int y, char b) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            char block = blocks[y * sizeX + x];
            if (b==0)b=BlockAir.chr;
            if(block!=(blocks[y*sizeX+x]=b)) {
                metas[y * sizeX + x] = 0;
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean set(int x, int y, char b,int meta) {
        if ((x|y)>=0&&x<sizeX&&y<sizeY){
            if (b==0) {
                b=BlockAir.chr;
            }
            blocks[y * sizeX + x]=b;
            metas[y*sizeX+x]=meta%metaStates(b);
            return true;
        }else{
            return false;
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
        StringBuilder str=new StringBuilder();
        for(int i=0; i<blocks.length; i++) {
            str.append(blocks[i]);
            int meta=metas[i];
            if (meta!=0) {
                str.append(meta);
            }
        }
        str.append("-").append(sizeX);
        str.append("-").append(sizeY);
        str.append("-").append(((int) (Utils.clamp(spawnPoint.x(),0,sizeX-spawnPoint.w())*100)));
        str.append("-").append(((int) (Utils.clamp(spawnPoint.y(),0,sizeY-spawnPoint.w())*100)));
        str.append("-").append(((int) (Utils.clamp(spawnPoint.w(),0,sizeX)*100)));
        str.append("-").append(((int) (Utils.clamp(spawnPoint.h(),0,sizeY)*100)));
        return Compresser.compress(str.toString());
    }
    public void loadWorldString(String s) {
        s=Compresser.decompress(s);
        String[] split=new String[]{"","","","","","",""};
        StringBuilder stringBuilder = new StringBuilder();
        int index=0;
        for (char c : s.toCharArray()) {
            if (c=='-'){
                split[index++]=stringBuilder.toString();
                stringBuilder.setLength(0);
                if (index>split.length) {
                    return;
                }
            }else {
                stringBuilder.append(c);
            }
        }
        //String[] split=s.split("-");
        setSize(Utils.parseInt(split[1],32),Utils.parseInt(split[2],18));
        spawnPoint.set(Utils.parseInt(split[3],10)/100f,Utils.parseInt(split[4],10)/100f,Utils.parseInt(split[5],80)/100f,Utils.parseInt(split[6],80)/100f);
        spawnPoint.round();
        char[] chars=split[0].toCharArray();
        int number=0;
        int i=0;
        char block=0;
        for(char c : chars) {
            if (c>='0'&&c<='9'){
                number=number*10+(c-'0');
            }else/* if((c>='A'&&c<='Z')||(c>='a'&&c<='z'))*/{
                if (block!=0){
                    blocks[i]=block;
                    metas[i]=number;
                    number=0;
                    i++;
                }
                block=c;
            }
        }
        if (block!=0){
            blocks[i]=block;
            metas[i]=number;
        }
    }



    private int metaStates(char block) {
        if (block== BlockOneWay.chr||block== BlockSpike.chr) {
            return 4;
        }
        return 1;
    }
}
