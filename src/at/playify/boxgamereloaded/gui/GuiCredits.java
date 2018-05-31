package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiCredits extends Gui {
    private Scroller scroller;
    private float aspect;
    private ArrayList<String> lst=new ArrayList<>();

    public GuiCredits(BoxGameReloaded game) {
        super(game);
        scroller=new Scroller(game,this);
        scroller.factor=1/(.05f*.9f/0.8f);
        scroller.setScroll(-5);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new Button(game) {
            @Override
            public String text(){
                return "";
            }

            @Override
            public BoundingBox3d bound(){
                bound.set(game.aspectratio/4,.1f,0.01f,game.aspectratio*3/4,.9f,0);
                return bound;
            }

            @Override
            public boolean click(Finger finger){
                return true;
            }

            @Override
            public void draw(Drawer d){
            }
        });
        buttons.add(new ButtonClose(game));
    }

    @Override
    public boolean clickButtons(Finger finger){
        boolean b=super.clickButtons(finger);
        if (!b) {
            game.gui.close(this);
        }
        return b;
    }

    @Override
    public void draw() {
        scroller.scrollMin=Integer.MIN_VALUE;
        scroller.scrollMax=Integer.MAX_VALUE;
        scroller.draw();
        if (aspect!=game.aspectratio){
            aspect=game.aspectratio;
            lst.clear();
            String s=game.handler.assetString("credits.txt");
            new TextCreator(game,lst).add(s);
        }
        game.d.cube(game.aspectratio/4,.1f,0.01f,game.aspectratio/2,.8f,.01f,0xFF00FFFF);
        float scroll=scroller.getScroll();
        for (int i=(int)scroll-1;i<Math.min(lst.size(),scroll+9);i++) {
            if (i>=0) {
                String s=lst.get(i);
                if (!s.isEmpty()) {
                    if (s.charAt(0)=='$') {
                        game.d.drawStringCenter(s.substring(1), game.aspectratio/2, .7f-((i+1)-scroll)*.05f*.9f/0.8f, .1f, 0x66000000);
                    } else {
                        game.d.drawString(s, game.aspectratio/4+.05f, .7f-(i-scroll)*.05f*.9f/0.8f, .05f, 0x66000000);
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
        game.d.drawStringCenter("Credits",game.aspectratio/2,.8f,.08f, 0x66000000);
        super.draw();
    }

    @Override
    public boolean click(Finger finger) {
        return scroller.click(finger);
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
        scroller.scroll(-.025f);
        float scroll=scroller.getScroll();
        if (scroll>lst.size())scroller.scroll(lst.size()+12);
        if (scroll<-12)scroller.scroll(-lst.size()-12);
        return false;
    }
    @Override
    public boolean scroll(float f) {
        scroller.scroll(2*f);
        return true;
    }
}
