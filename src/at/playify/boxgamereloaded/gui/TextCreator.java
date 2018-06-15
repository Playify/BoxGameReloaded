package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;

import java.util.ArrayList;

public class TextCreator {
    private StringBuilder str=new StringBuilder();
    public ArrayList<String> lst;
    private BoxGameReloaded game;

    public TextCreator(BoxGameReloaded game, ArrayList<String> lst) {
        this.lst=lst;
        this.game=game;
        lst.clear();
    }

    public void add(String s){
        float w=0,maxW=game.aspectratio/2-.11f;
        String sub;
        for (char c : s.toCharArray()) {
            float cw=game.d.charWidth(c);
            if ((w+cw)*0.05f>maxW||c=='\n') {
                sub=str.toString();
                lst.add(sub);
                if (!sub.isEmpty()&&sub.charAt(0)=='$') lst.add("");
                w=0;
                str.setLength(0);
            }
            if (c!='\n') {
                w+=cw;
                str.append(c);
            }
        }
        sub=str.toString();
        lst.add(sub);
        if (!sub.isEmpty()&&sub.charAt(0)=='$') lst.add("");
        str.setLength(0);
    }
}
