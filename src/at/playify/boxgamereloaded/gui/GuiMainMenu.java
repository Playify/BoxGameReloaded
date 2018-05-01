package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.gui.button.LevelButton;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;

import java.util.ArrayList;

public class GuiMainMenu extends Gui {
    private float scroll;
    private boolean clicked;
    private float lasty;

    GuiMainMenu(BoxGameReloaded game) {
        super(game);
    }

    private Finger fake=new Finger(0);

    @Override
    public void initGui(ArrayList<Button> buttons) {
        for (int i=0;i<12;i++) {
            float x=(i%4)/10f;
            float y=(2-i/4)/10f;
            buttons.add(new LevelButton(game, "paint_"+i, .2f+x*4, .1f+y*3));
        }
        //TODO buttons.add(new ScrollList(game));
    }

    @Override
    public void draw() {
        final float v=1;
        game.d.cube(-v, -v, 1, game.aspectratio+2*v, 1+2*v, 0, 0xFF000000, false, false, false, false, true, false);
        game.d.pushMatrix();
        game.d.translate(0, scroll, 0);
        super.draw();
        game.d.popMatrix();
        Finger finger=game.fingers[0];
        if (!finger.down) clicked=false;
        if (clicked) {
            float dy=lasty-finger.getY();
            scroll=Utils.clamp(scroll+dy/game.d.getHeight(), 0, 1);
            lasty=finger.getY();
        }
    }

    @Override
    public boolean click(Finger finger) {
        fake.setX(finger.getX());
        fake.setY(finger.getY()+scroll*game.d.getHeight());
        fake.down=finger.down;
        fake.dx=finger.dx;
        fake.dy=finger.dy;
        fake.index=finger.index;
        if (!super.click(fake)&&finger.index==0&&finger.down) {
            clicked=true;
            lasty=finger.getY();
        }
        return true;
    }

    @Override
    public boolean tick() {
        game.paused=true;
        return super.tick();
    }
}
