package at.playify.boxgamereloaded.level.compress;

import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class CompresserFallback implements Compresser {
    @Override
    public String compress(LevelData data) {
        StringBuilder str=new StringBuilder();
        while(data.hasNext()) {
            data.next();
            str.append(data.chr());
            int meta=data.meta();
            if (meta!=0) {
                str.append(meta);
            }
        }
        str.append("+").append(data.sizeX);
        str.append("+").append(data.sizeY);
        str.append("+").append((Utils.round(data.spawnPoint.x()*100)));
        str.append("+").append((Utils.round(data.spawnPoint.y()*100)));
        str.append("+").append((Utils.round(data.spawnPoint.w()*100)));
        str.append("+").append((Utils.round(data.spawnPoint.h()*100)));
        return str.toString();
    }

    @Override
    public LevelData decompress(String s) {
        String[] split=new String[]{"","","","","","",""};
        StringBuilder stringBuilder = new StringBuilder();
        int index=0;
        for (char c : s.toCharArray()) {
            if (c == '+') {
                split[index++]=stringBuilder.toString();
                stringBuilder.setLength(0);
                if (index>split.length) {
                    break;
                }
            }else {
                stringBuilder.append(c);
            }
        }
        int sizeX=Utils.parseInt(split[1],32);
        int sizeY=Utils.parseInt(split[2],18);
        char[] blocks=new char[sizeX*sizeY];
        int[] metas=new int[sizeX*sizeY];
        RectBound spawnPoint=new RectBound();
        spawnPoint.set(Utils.parseInt(split[3],10)/100f,Utils.parseInt(split[4],10)/100f,Utils.parseInt(split[5],80)/100f,Utils.parseInt(split[6],80)/100f);
        spawnPoint.round();
        char[] chars=split[0].toCharArray();
        int number=0;
        int i=0;
        char block=0;
        for(char c : chars) {
            if (c>='0'&&c<='9'){
                number=number*10+(c-'0');
            }else{
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
        return new LevelData(blocks,metas,spawnPoint,sizeX,sizeY);
    }

    @Override
    public String version() {
        return null;
    }
}
