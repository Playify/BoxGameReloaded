package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.LevelButton;
import at.playify.boxgamereloaded.gui.button.StageSelector;
import at.playify.boxgamereloaded.network.packet.PacketMainMenu;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiMainMenu extends Gui {
    public float scroll;
    private boolean clicked;
    private float lasty;
    private float lastx;
    private float moved=0;

    GuiMainMenu(BoxGameReloaded game) {
        super(game);
        if (game.connection!=null) {
            game.connection.sendPacket(new PacketMainMenu());
        }
    }

    @Override
    public void initGui(final ArrayList<Button> buttons) {
        for (int i=0;i<7;i++) {
            buttons.add(new LevelButton(game, i, this));
        }
        buttons.add(new StageSelector(game, this));
    }

    @Override
    public void draw() {
        final float v=1;
        game.d.cube(-v, -v, 1, game.aspectratio+2*v, 1+2*v, 0, 0xFF000000, false, false, false, false, true, false);
        super.draw();
        Finger finger=game.fingers[0];
        if (!finger.down&&clicked){
            clicked=false;
            if (moved<game.d.getHeight()/50f){
                super.click(finger);
            }
            moved=0;
        }
        if (clicked) {
            float dx=lastx-finger.getX();
            float dy=lasty-finger.getY();
            scroll+=dy/game.d.getHeight();
            moved+=dx*dx+dy*dy;
            lastx=finger.getX();
            lasty=finger.getY();
            game.pauseLock.unlock();
        }
    }

    @Override
    public boolean click(Finger finger) {
        if (finger.index==0&&finger.down) {
            clicked=true;
            lasty=finger.getY();
            lastx=finger.getX();
        }
        return true;
    }

    @Override
    public boolean tick() {
        float shouldScroll=scroll;
        int index=game.levels.containsKey(game.vars.stage)? game.levels.get(game.vars.stage).size():0;
        float scrollMax=(index*(.2f+.05f))-.225f;
        //scrollMax-=.25f;
        if(scroll<0)shouldScroll=0;
        if(scroll>scrollMax)shouldScroll=scrollMax;
        if (!clicked) {
            scroll+=(shouldScroll-scroll)/10;
        }
        if (Math.abs(shouldScroll-scroll)<1E-20f)scroll=shouldScroll;
        game.paused=true;
        return super.tick()&&scroll==shouldScroll;
    }
}
