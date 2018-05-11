package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiCredits extends Gui {
    private float aspect;
    private ArrayList<String> lst=new ArrayList<>();
    private float scroll=-5;

    public GuiCredits(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ButtonClose(game));
    }

    @Override
    public void draw() {
        if (aspect!=game.aspectratio){
            aspect=game.aspectratio;
            lst.clear();
            StringBuilder str=new StringBuilder();
            float w=0,maxW=game.aspectratio/2-.1f;
            String s=game.handler.assetString("credits.txt");
            for (char c : s.toCharArray()) {
                if (c=='\r') continue;
                float cw=game.d.charWidth(c);
                if ((w+cw)*0.05f>maxW||c=='\n') {
                    String sub=str.toString();
                    lst.add(sub);
                    if (!sub.isEmpty()&&sub.charAt(0)=='$') lst.add("");
                    w=0;
                    str.setLength(0);
                }
                if (c!='\n'){
                    w+=cw;
                    str.append(c);
                }
            }
            lst.add(str.toString());
        }
        game.d.cube(game.aspectratio/4,.1f,0.01f,game.aspectratio/2,.8f,.01f,0xFF00FFFF);
        for (int i=(int)scroll-1;i<Math.min(lst.size(),scroll+9);i++) {
            if (i>=0) {
                String s=lst.get(i);
                if (!s.isEmpty()) {
                    if (s.charAt(0)=='$') {
                        game.d.drawStringCenter(s.substring(1), game.aspectratio/2, .7f-((i+1)-scroll)*.05f*.9f/0.8f, .1f);
                    } else {
                        game.d.drawString(s, game.aspectratio/4+.05f, .7f-(i-scroll)*.05f*.9f/0.8f, .05f);
                    }
                }
            }
        }
        game.d.clearDepth();
        //blue overlay to hide text
        game.d.cube(game.aspectratio/4,.1f,0.01f,game.aspectratio/2,.14f,.01f,0xFF00FFFF,false,false,false,false);
        game.d.cube(game.aspectratio/4,.76f,0.01f,game.aspectratio/2,.14f,.01f,0xFF00FFFF,false,false,false,false);
        //white border
        game.d.cube(game.aspectratio/4,.76f,0,game.aspectratio/2,.005f,.005f,0xFFFFFFFF,true,false,true,false);
        game.d.cube(game.aspectratio/4,.235f,0,game.aspectratio/2,.005f,.005f,0xFFFFFFFF,true,false,true,false);
        game.d.cube(game.aspectratio*3/4-.005f,.235f,0,.005f,.525f,.005f,0xFFFFFFFF,false,true,false,true);
        game.d.cube(game.aspectratio/4,.235f,0,.005f,.525f,.005f,0xFFFFFFFF,false,true,false,true);
        game.d.drawStringCenter("Credits",game.aspectratio/2,.8f,.08f);
        super.draw();
    }

    @Override
    public boolean click(Finger finger) {
        super.click(finger);
        return true;
    }


    private class ButtonClose extends Button {
        public ButtonClose(BoxGameReloaded game) {
            super(game);
        }

        @Override
        public String text() {
            return "Close";
        }

        @Override
        public BoundingBox3d bound() {
            bound.set(game.aspectratio/4+.02f,.12f,0,game.aspectratio/2-.01f,.22f,0.01f);
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            game.gui.close(GuiCredits.this);
            return true;
        }
    }

    @Override
    public boolean tick() {
        scroll+=.05f;
        if (scroll>lst.size())scroll=scroll-lst.size()-12;
        return false;
    }
}
