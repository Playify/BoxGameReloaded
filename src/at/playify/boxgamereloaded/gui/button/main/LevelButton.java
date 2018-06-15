package at.playify.boxgamereloaded.gui.button.main;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiMainMenu;
import at.playify.boxgamereloaded.gui.button.Button;
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
    public String genText() {
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
                    s=game.vars.stage+index+"="+s+"=";
                }
                return s;
            }else{
                return "==";
            }
        }
        return game.vars.stage+index+"="+game.vars.stage+" "+index+"=";
    }

    @Override
    public BoundingBox3d genBound() {
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
                if (index<list.size()) {
                    float dx=(1-main.uiState)*(game.aspectratio/2+.1f);
                    buttonBound.set(game.aspectratio/2+0.05f+dx, y+scroll, 0, game.aspectratio-.05f+dx, y+height+scroll, 0.025f);
                    return buttonBound;
                }
            }
        }
        buttonBound.set(0,0,0,0,0,0.025f);
        return buttonBound;
    }

    @Override
    public boolean click(Finger finger) {
        if (main.uiState!=1) return false;
        if (finger.y<game.d.getHeight()*.2f) {
            return false;
        }
        String text=genText();
        boolean red=text.charAt(text.indexOf('=')+1)=='$';
        if (!red) {
            game.joinWorld(text.substring(0, text.indexOf('=')));
        }
        return true;
    }

    @Override
    public void draw(Drawer d) {
        if (main.uiState==0) return;
        d.pushMatrix();
        BoundingBox3d bound=genBound();
        d.cube(0, 0, 0, 1, 1, 1, genColor());
        float v=(bound.maxY-bound.minY);
        float v2=(bound.maxX-bound.minX);
        d.scale(1/v2, 1/v,1);
        String text=genText();
        boolean red=text.charAt(text.indexOf('=')+1)=='$';
        d.drawStringCenter(text.substring(text.indexOf('=')+(red?2:1), text.lastIndexOf('=')), v2/2, v/3, v/3, red ?0xFFFF0000:0x66000000);
        String by=text.substring(text.lastIndexOf('=')+1);
        if (!by.isEmpty()) {
            by="by "+by;
            float h=v/9;
            d.drawString(by, v2-v/18-(d.getStringWidth(by))*h, v/18, h, 0x66000000);
        }
        d.popMatrix();
    }
}
