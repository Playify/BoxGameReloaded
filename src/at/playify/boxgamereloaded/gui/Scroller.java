package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;

public class Scroller {
    public float factor=1;
    BoxGameReloaded game;
    private boolean clicked;
    private float moved;
    private Gui gui;
    private float lasty;
    private float lastx;
    private boolean ticked;
    public float scrollMax;
    public float scrollMin;

    public Scroller(BoxGameReloaded game, Gui gui) {
        this.game=game;
        this.gui=gui;
    }

    private float scroll;

    public void draw(){
        Finger finger=game.fingers[0];
        if (!finger.down&&clicked) {
            clicked=false;
            if (moved<game.d.getHeight()/25f) {
                gui.clickButtons(finger);
            }
            moved=0;
        }
        if (clicked&&((!(gui instanceof GuiMainMenu))||((GuiMainMenu) gui).ui)) {
            float dx=lastx-finger.getX();
            float dy=lasty-finger.getY();
            scroll+=(dy/game.d.getHeight())*factor;
            if (!ticked){
                if(scroll<scrollMin)scroll=scrollMin;
                if(scroll>scrollMax)scroll=scrollMax;
            }
            moved+=dx*dx+dy*dy;
            lastx=finger.getX();
            lasty=finger.getY();
            game.pauseLock.unlock();
        }

    }

    public float getScroll() {
        if (!ticked){
            if(scroll<scrollMin)scroll=scrollMin;
            if(scroll>scrollMax)scroll=scrollMax;
        }
        return scroll;
    }

    public boolean click(Finger finger) {
        if (finger.index==0&&finger.down) {
            clicked=true;
            lasty=finger.getY();
            lastx=finger.getX();
            moved=0;
        }
        return true;
    }

    public void scroll(float f) {
        scroll-=f;
        if (!ticked){
            if(scroll<scrollMin)scroll=scrollMin;
            if(scroll>scrollMax)scroll=scrollMax;
        }
    }

    public boolean tick(){
        ticked=true;

        float shouldScroll=scroll;
        //scrollMax-=.25f;
        if(scroll<scrollMin)shouldScroll=scrollMin;
        if(scroll>scrollMax)shouldScroll=scrollMax;
        if (!clicked) {
            scroll+=(shouldScroll-scroll)/10;
        }
        if (Math.abs(shouldScroll-scroll)<1E-20f){
            scroll=shouldScroll;
            return true;
        }
        return false;
    }

    public void setScroll(float scroll) {
        this.scroll=scroll;
        if(scroll<scrollMin)this.scroll=scrollMin;
        if(scroll>scrollMax)this.scroll=scrollMax;
    }
}
