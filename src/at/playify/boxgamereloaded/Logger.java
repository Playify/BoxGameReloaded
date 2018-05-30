package at.playify.boxgamereloaded;

import at.playify.boxgamereloaded.util.Utils;

import java.util.ArrayList;

public class Logger {

    private ArrayList<Line> lines=new ArrayList<>();

    public void error(String s){
        lines.add(new Line(s,1));
        System.err.println(s);
    }
    public void error(Exception e){
        String s=e.getClass().getSimpleName()+":"+e.getMessage();
        lines.add(new Line(s,1));
        System.err.println(s);
    }
    public void show(String s){
        lines.add(new Line(s));
        System.out.println(s);
    }

    public void draw(BoxGameReloaded game){
        int size=lines.size();
        for (int i=0;i<size;i++) {
            try {
                Line line=lines.get(i);
                if (line.time<0) {
                    lines.remove(i);
                    i--;
                } else {
                    final float h=.05f;
                    game.d.drawString(line.s, .01f, .01f+h*(size-i-1), h, color(line));
                }
            }catch (IndexOutOfBoundsException ignoredö){ }
        }
    }

    private int color(Line line) {
        int color=line.type==1?0xFF0000:0xFFFFFF;
        color|=((int)(255f*Utils.clamp(line.time,0,10)/10))<<24;
        return color;
    }

    public boolean tick(){
        boolean b=true;
        for (int i=0;i<lines.size();i++) {
            Line line=lines.get(i);
            line.time--;
            if (b&&line.time>0) {
                b=false;
            }
        }
        return !b;
    }

    private class Line{
        private String s;
        private int time;
        private int type;

        public Line(String s) {
            this.s=s;
            this.type=0;
            this.time=200;
        }
        public Line(String s,int type) {
            this.s=s;
            this.type=type;
            this.time=200;
        }
    }
}