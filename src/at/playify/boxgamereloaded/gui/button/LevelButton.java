package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiMainMenu;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class LevelButton extends Button {
    private final int index;
    private GuiMainMenu main;

    public LevelButton(BoxGameReloaded game,int index, GuiMainMenu main) {
        super(game);
        this.index=index;
        this.main=main;
    }

    @Override
    public String text() {
        int index=this.index;
        float scroll=main.scroller.getScroll();
        float height=.2f;
        float y=1-(index*(height+.05f))-height-.2f;
        while(y+scroll>1.5f){
            y-=1.75f;
            index+=7;
        }
        if (game.levels.containsKey(game.vars.stage)){
            ArrayList<String> list=game.levels.get(game.vars.stage);
            if (index<list.size()) {
                String s=list.get(index);
                if (s.indexOf('=')==-1) {
                    s=game.vars.stage+index+"="+s;
                }
                return s;
            }else{
                return "=";
            }
        }
        return game.vars.stage+index+"="+game.vars.stage+index;
    }

    @Override
    public BoundingBox3d bound() {
        if (game.levels.containsKey(game.vars.stage)){
            ArrayList<String> list=game.levels.get(game.vars.stage);
            if (index<list.size()) {
                int index=this.index;
                float scroll=main.scroller.getScroll();
                float height=.2f;
                float y=1-(index*(height+.05f))-height-.2f-.05f;
                while(y+scroll>1.5f){
                    y-=1.75f;
                    index+=7;
                }
                float dx=(1-main.uiState)*(game.aspectratio/2+.1f);
                bound.set(game.aspectratio/2+0.05f+dx, y+scroll, 0, game.aspectratio-.05f+dx, y +height+scroll, 0.025f);
                return bound;
            }
        }
        bound.set(0,0,0,0,0,0.025f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        if (main.uiState!=1) return false;
        if (finger.getY()<game.d.getHeight()*.2f) {
            return false;
        }
        String text=text();
        game.joinWorld(text.substring(0,text.indexOf('=')));
        game.gui.closeMainMenu();
        game.paused=false;
        game.pauseLock.unlock();
        return true;
    }

    @Override
    public void draw(Drawer d) {
        if (main.uiState==0) return;
        d.pushMatrix();
        BoundingBox3d bound=bound();
        d.cube(0, 0, 0, 1, 1, 1, color());
        float v=(bound.maxY-bound.minY);
        d.scale(1, 1/v,1);
        String text=text();
        d.drawStringCenter(text.substring(text.indexOf('=')+1), .5f, v/3, v/3);
        d.popMatrix();
    }
}
