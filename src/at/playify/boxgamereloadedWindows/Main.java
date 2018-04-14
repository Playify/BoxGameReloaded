package at.playify.boxgamereloadedWindows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {

    private static int[] chars=new int[Character.MAX_CODE_POINT];
    private static Finger finger;
    private static BoxGameReloaded game;

    public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(1920/2,1080/2));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        game=new BoxGameReloaded(new WindowsHandler());
        game.d=new WindowsDrawer(game);
        finger=game.fingers[0];
        game.start();
        game.vars.tickOnDraw=false;
        game.setCanDraw();

        while (!Display.isCloseRequested()) {
            runTick();
        }

        Display.destroy();
        System.exit(0);
    }


    //Tick ausführen
    private static void runTick() {
        game.d.setWidth(Display.getWidth());
        game.d.setHeight(Display.getHeight());
        game.draw();
        Display.update();
        Display.setResizable(true);
        //Tastatur
        Keyboard.enableRepeatEvents(true);
        while (Keyboard.next()) {
            int eventKey = Keyboard.getEventKey();
            int eventChar=Keyboard.getEventCharacter();
            if (eventKey== Keyboard.KEY_ESCAPE)eventChar='p';
            if (eventChar==0){
                eventChar=chars[eventKey];
            }else{
                chars[eventKey]=eventChar;
            }
            game.setKey(eventChar,Keyboard.getEventKeyState());
        }
        //Maus
        finger.x=Mouse.getX();//Finger wegen MultiTouch am Handy
        finger.y=Display.getHeight()-Mouse.getY();
        while (Mouse.next()) {
            if (Mouse.getEventButton()==0) {
                boolean down = Mouse.getEventButtonState();
                finger.x=Mouse.getEventX();
                finger.y=Display.getHeight()-Mouse.getEventY();
                finger.down=down;
                game.fingerStateChanged(finger);
            }else{
                finger.x=Mouse.getEventX();
                finger.y=Display.getHeight()-Mouse.getEventY();
            }
        }
    }
}
