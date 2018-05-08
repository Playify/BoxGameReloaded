package at.playify.boxgamereloaded.level.compress;

import at.playify.boxgamereloaded.util.Utils;
import at.playify.boxgamereloaded.util.bound.RectBound;

public class Compresser0 implements Compresser {
    @Override
    public String compress(LevelData data) {
        StringBuilder str=new StringBuilder();
        str.append(data.sizeX).append(".");
        str.append(data.sizeY).append(".");
        str.append((Utils.round(data.spawnPoint.x()*100))).append(".");
        str.append((Utils.round(data.spawnPoint.y()*100))).append(".");
        str.append((Utils.round(data.spawnPoint.w()*100))).append(".");
        str.append((Utils.round(data.spawnPoint.h()*100))).append(".");
        char lst='\0';
        int lstmeta=-1;
        int count=0;
        while (data.hasNext()) {
            data.next();
            if (data.chr()==lst&&data.meta()==lstmeta) {
                count++;
            } else {
                if (count!=0) {
                    if (count!=1) str.append(count);
                    str.append(lst);
                    if (lstmeta!=0) str.append(lstmeta).append('.');
                }
                lst=data.chr();
                lstmeta=data.meta();
                count=1;
            }
        }
        if (count!=0) {
            if (count!=1) str.append(count);
            str.append(lst);
            if (lstmeta!=0) str.append(lstmeta);
        }

        return str.toString();
    }

    @Override
    public LevelData decompress(String s) {
        int inde=0;
        int sizeX=Utils.parseInt(s.substring(0, inde=s.indexOf('.', inde+1)), 32);
        int sizeY=Utils.parseInt(s.substring(inde+1, inde=s.indexOf('.', inde+1)), 18);
        RectBound spawnPoint=new RectBound(Utils.parseInt(s.substring(inde+1, inde=s.indexOf('.', inde+1)), 10)/100f
                , Utils.parseInt(s.substring(inde+1, inde=s.indexOf('.', inde+1)), 10)/100f
                , Utils.parseInt(s.substring(inde+1, inde=s.indexOf('.', inde+1)), 80)/100f
                , Utils.parseInt(s.substring(inde+1, inde=s.indexOf('.', inde+1)), 80)/100f);
        spawnPoint.round();
        char[] sChars=s.substring(inde+1).toCharArray();
        char[] blk=new char[sizeX*sizeY];
        int[] mta=new int[sizeX*sizeY];
        int index=0,count=0;
        boolean number=false;
        for (int i=0;i<sChars.length;i++) {
            char c=sChars[i];
            if (Character.isDigit(c)) {
                number=true;
                count=10*count+(c-'0');
            } else if (Character.isLetter(c)) {
                boolean b=true;
                for (int j=i+1;j<sChars.length;j++) {
                    if(Character.isLetter(sChars[j])){
                        b=false;
                        break;
                    }
                    else if (sChars[j]=='.'){
                        break;
                    }
                }
                int meta=0;
                if (b){
                    while (i<sChars.length&&sChars[i]!='.'){
                        if (Character.isDigit(sChars[i]))meta=10*meta+(sChars[i]-'0');
                        i++;
                    }
                }
                if (!number) count=1;
                for (int j=0;j<count;j++) {
                    blk[index]=c;
                    mta[index]=meta;
                    index++;
                }
                number=false;
                count=0;
            }
        }
        return new LevelData(blk,mta,spawnPoint,sizeX,sizeY);
    }

    @Override
    public String version() {
        return "0";
    }
}
