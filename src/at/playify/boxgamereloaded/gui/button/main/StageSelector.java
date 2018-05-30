package at.playify.boxgamereloaded.gui.button.main;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiMainMenu;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class StageSelector extends Button {
    private GuiMainMenu main;

    public StageSelector(BoxGameReloaded game, GuiMainMenu main) {
        super(game);
        this.main=main;
    }

    @Override
    public String text() {
        return game.vars.stage==null?"":game.vars.stage;
    }

    @Override
    public BoundingBox3d bound() {
        float dx=(1-main.uiState)*(game.aspectratio/2+.2f);
        bound.set(game.aspectratio/2+dx, .8f, -0.05f, game.aspectratio+dx, 1, 0.05f);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        if (main.uiState==0) return false;
        ArrayList<String> list=game.levels.get("list");
        if (list!=null&&!list.isEmpty()) {
            if (finger.x-game.d.getWidth()*3/4f<0) {
                int i=list.indexOf(game.vars.stage)-1;
                if (i<0) i=list.size()-1;
                game.vars.stage=list.get(i);
            } else {
                int i=list.indexOf(game.vars.stage)+1;
                if (i >= list.size()) i=0;
                game.vars.stage=list.get(i);
            }
            game.vars.loader.save();
        }
        return true;
    }

    @Override
    public void draw(Drawer d) {
        //if (main.uiState==0) return;
        int color=color();
        d.cube(0,0,2/3f,1,1,1/3f, color,true,false,true,false);
        d.cube(0,0,1/6f,1,1,1/6f, color,true,false,true,false);
        d.cube(0,0,1/6f,.1f/game.aspectratio,1,5/6f, color);
        d.cube(1-.1f/game.aspectratio,0,1/6f,.1f/game.aspectratio,1,5/6f, color);
        d.cube(0,0,1/6f,.1f/game.aspectratio,1,5/6f, color);
        game.d.translate(0,0,1/6f);
        float v=(bound.maxY-bound.minY);
        d.scale(1, 1/v,1);
        d.pushMatrix();
        d.depth(false);
        d.scale(v,v,1);
        d.translate(.5f,.5f,0);
        d.rotate(270,0,0,1);
        d.vertex(game.vertex.arrow,0xFF0000FF,1,.4f);
        d.rotate(180,0,0,1);
        d.scale(1/v,1/v,1);
        d.translate(0,-1,0);
        d.scale(v,v,1);
        d.translate(0,1,0);
        d.vertex(game.vertex.arrow,0xFF0000FF,1,.4f);
        d.depth(true);
        d.popMatrix();
        d.drawStringCenter(game.vars.stage, .5f, v/4, v/2, 0x66000000);
    }

    @Override
    public int color() {
        return 0xFF004A60;
    }
}
