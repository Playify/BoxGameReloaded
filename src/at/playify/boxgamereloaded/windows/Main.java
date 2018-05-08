package at.playify.boxgamereloaded.windows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    private static int[] chars=new int[Character.MAX_CODE_POINT];
    private static Finger finger;
    private static BoxGameReloaded game;
    static final File base=new File(System.getenv("APPDATA"), "BoxGameReloaded");
    private static boolean down, zooming;
    private static float lx, ly;

    public static void main(String[] args) {
        loadLibrary();
        try {
            Display.setDisplayMode(new DisplayMode(1920/2,1080/2));
            Display.create();
            Display.setVSyncEnabled(false);
            Display.setResizable(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        WindowsHandler handler;
        game=new BoxGameReloaded(handler=new WindowsHandler());
        handler.game = game;
        game.d = handler.d = new WindowsDrawer(game);
        finger=game.fingers[0];
        game.start();
        game.vars.tickOnDraw=false;

        while (!Display.isCloseRequested()) {
            runTick();
        }

        Display.destroy();
        System.exit(0);
    }

    //Librarys von Jar-Datei extrahieren um von LWJGL,OpenGL,... genutzt zu werden.
    private static void loadLibrary() {
        try {
            File base=Main.base;
            File libs = new File(base, "libs");
            if (!libs.exists()) {
                libs.mkdirs();
            }
            System.setProperty("java.library.path", libs.getAbsolutePath());
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (file.isFile()) {
                ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
                ZipEntry e;
                byte[] buffer = new byte[1024];
                while ((e = zip.getNextEntry()) != null) {
                    if ((e.getName().startsWith("libs/") || e.getName().startsWith("assets/")) && !e.isDirectory()) {
                        int len;
                        File lib = new File(libs, e.getName().substring(e.getName().indexOf('/') + 1));
                        if (!lib.exists()) {
                            FileOutputStream fos = new FileOutputStream(lib);
                            while ((len = zip.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                        }
                    }
                }
            } else {
                File[] zip = new File(file, "libs").listFiles();
                if (zip != null) {
                    for (File lib : zip) {
                        File toLib = new File(libs, lib.getName());
                        if (!toLib.exists()) {
                            Files.copy(lib.toPath(), toLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Tick ausführen
    private static void runTick() {
        game.d.setWidth(Display.getWidth());
        game.d.setHeight(Display.getHeight());
        try {
            game.draw();
        } catch (Exception e) {
            System.err.println("Drawing Error");
            e.printStackTrace();
        }
        try {
            Display.update();
            //Tastatur
            Keyboard.enableRepeatEvents(true);
            while (Keyboard.next()) {
                int eventKey=Keyboard.getEventKey();
                if (Keyboard.getEventKeyState()) {
                    switch (eventKey) {
                        case Keyboard.KEY_ESCAPE:
                            game.cheatCode('s');
                            break;
                        case Keyboard.KEY_P:
                            game.cheatCode('b');
                            break;
                        case Keyboard.KEY_O:
                            game.cheatCode('a');
                            break;
                        case Keyboard.KEY_UP:
                            game.cheatCode('u');
                            break;
                        case Keyboard.KEY_DOWN:
                            game.cheatCode('d');
                            break;
                        case Keyboard.KEY_LEFT:
                            game.cheatCode('l');
                            break;
                        case Keyboard.KEY_RIGHT:
                            game.cheatCode('r');
                            break;
                        case Keyboard.KEY_SPACE:
                            game.cheatCode('u');
                            break;
                        case Keyboard.KEY_W:
                            game.cheatCode('u');
                            break;
                        case Keyboard.KEY_A:
                            game.cheatCode('l');
                            break;
                        case Keyboard.KEY_S:
                            game.cheatCode('d');
                            break;
                        case Keyboard.KEY_D:
                            game.cheatCode('r');
                            break;
                    }
                }
                int eventChar=Keyboard.getEventCharacter();
                if (Keyboard.getKeyName(eventKey).length()==1) {
                    eventChar=Keyboard.getKeyName(eventKey).charAt(0);
                }
                if (eventKey==Keyboard.KEY_ESCAPE) eventChar=257;
                if (eventChar==0) {
                    eventChar=chars[eventKey];
                } else {
                    chars[eventKey]=eventChar;
                }
                game.setKey(eventChar, Keyboard.getEventKeyState());
            }
            //Maus
            //finger.set(Mouse.getX(),Display.getHeight()-Mouse.getY());//Finger wegen MultiTouch am Handy
            while (Mouse.next()) {
                if (Mouse.getEventButton()==0) {
                    boolean down=Mouse.getEventButtonState();
                    finger.set(Mouse.getEventX(), Display.getHeight()-Mouse.getEventY());
                    if (down)
                        finger.setDown(Mouse.getEventX(), Display.getHeight()-Mouse.getEventY(), game);
                    finger.down=down;
                    game.fingerStateChanged(finger);
                } else {
                    finger.set(Mouse.getEventX(), Display.getHeight()-Mouse.getEventY());
                }
            }
            int wheel=Mouse.getDWheel();
            if (game.gui.isMainMenuVisible()&&wheel!=0){
                game.gui.main.scroll-=wheel*0.001f;
                game.pauseLock.unlock();
            }else if (game.painter.draw) {
                float v=wheel*0.001f+1;
                game.zoom=Utils.clamp(game.zoom*v, 0.3f, 5f);

                if (down!=(Mouse.isButtonDown(1)&&!finger.control)) {
                    down^=true;
                    if (down) {
                        lx=finger.getX();
                        ly=finger.getY();
                        zooming=finger.getX()/game.d.getHeight()<1/7f;
                    }
                }
                if (down) {
                    if (zooming) {
                        float zoom=game.zoom*((ly-finger.getY())/game.d.getHeight()+1);
                        ly=finger.getY();
                        game.zoom=Utils.clamp(zoom, 0.3f, 5f);
                    } else {
                        float w=game.d.getWidth(), h=game.d.getHeight();
                        float x=(lx-finger.getX())*game.vars.display_size*game.aspectratio/(w*game.zoom)+game.zoom_x;
                        float y=-(ly-finger.getY())*game.vars.display_size/(h*game.zoom)+game.zoom_y;

                        lx=finger.getX();
                        ly=finger.getY();


                        game.zoom_x=Utils.clamp(x, 0, game.level.sizeX);
                        game.zoom_y=Utils.clamp(y, 0, game.level.sizeY);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Input Exception");
            e.printStackTrace();
        }
    }
}
