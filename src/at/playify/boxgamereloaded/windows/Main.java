package at.playify.boxgamereloaded.windows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Keymap;
import at.playify.boxgamereloaded.util.Finger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    private static Finger finger;
    private static BoxGameReloaded game;
    static final File base=new File(System.getenv("APPDATA"), "BoxGameReloaded");
    private static WindowsDrawer d;

    public static void main(String[] args) {
        loadLibrary();
        try {
            Display.setDisplayMode(new DisplayMode(1920/2, 1080/2));
            Display.create();
            Display.setVSyncEnabled(false);
            Display.setResizable(true);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        WindowsHandler handler;
        game=new BoxGameReloaded(handler=new WindowsHandler());
        handler.game=game;
        game.setDrawer(d=handler.d=new WindowsDrawer(game));
        finger=game.finger(0);
        game.start();

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
            File libs=new File(base, "libs");
            if (!libs.exists()) {
                libs.mkdirs();
            }
            System.setProperty("org.lwjgl.librarypath", libs.getAbsolutePath());
            File file=new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (file.isFile()) {
                try (ZipInputStream zip=new ZipInputStream(new FileInputStream(file))) {
                    ZipEntry e;
                    byte[] buffer=new byte[1024];
                    while ((e=zip.getNextEntry())!=null) {
                        if (e.getName().startsWith("libs/")&&!e.isDirectory()) {
                            int len;
                            File lib=new File(libs, e.getName().substring(e.getName().indexOf('/')+1));
                            if (!lib.exists()) {
                                FileOutputStream fos=new FileOutputStream(lib);
                                while ((len=zip.read(buffer))>0) {
                                    fos.write(buffer, 0, len);
                                }
                                fos.close();
                            }
                        }
                    }
                }
            } else {
                File[] zip=new File(file, "libs").listFiles();
                if (zip!=null) {
                    for (File lib : zip) {
                        File toLib=new File(libs, lib.getName());
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
        d.w=Display.getWidth();
        d.h=Display.getHeight();
        try {
            game.draw();
        } catch (Exception e) {
            game.logger.error("Drawing Error");
            e.printStackTrace();
        }
        try {
            Display.update();
            //Tastatur
            Keyboard.enableRepeatEvents(true);
            while (Keyboard.next()) {
                game.setKey(convert(Keyboard.getEventKey()), Keyboard.getEventKeyState());
            }
            //Maus
            //finger.set(Mouse.getX(),Display.getHeight()-Mouse.getY());//Finger wegen MultiTouch am Handy
            while (Mouse.next()) {
                if (Mouse.getEventButton()==0) {
                    boolean down=Mouse.getEventButtonState();
                    finger.x=Mouse.getEventX();
                    finger.y=Display.getHeight()-Mouse.getEventY();
                    finger.down=down;
                    game.fingerStateChanged(finger);
                } else {
                    finger.x=Mouse.getEventX();
                    finger.y=Display.getHeight()-Mouse.getEventY();
                }
            }
            int wheel=Mouse.getDWheel();
            game.scroll(wheel);
        } catch (Exception e) {
            game.logger.error("Input Exception");
            e.printStackTrace();
        }
    }

    private static char convert(int eventKey) {
        switch (eventKey) {
            case Keyboard.KEY_A: return Keymap.KEY_A;
            case Keyboard.KEY_B: return Keymap.KEY_B;
            case Keyboard.KEY_C: return Keymap.KEY_C;
            case Keyboard.KEY_D: return Keymap.KEY_D;
            case Keyboard.KEY_E: return Keymap.KEY_E;
            case Keyboard.KEY_F: return Keymap.KEY_F;
            case Keyboard.KEY_G: return Keymap.KEY_G;
            case Keyboard.KEY_H: return Keymap.KEY_H;
            case Keyboard.KEY_I: return Keymap.KEY_I;
            case Keyboard.KEY_J: return Keymap.KEY_J;
            case Keyboard.KEY_K: return Keymap.KEY_K;
            case Keyboard.KEY_L: return Keymap.KEY_L;
            case Keyboard.KEY_M: return Keymap.KEY_M;
            case Keyboard.KEY_N: return Keymap.KEY_N;
            case Keyboard.KEY_O: return Keymap.KEY_O;
            case Keyboard.KEY_P: return Keymap.KEY_P;
            case Keyboard.KEY_Q: return Keymap.KEY_Q;
            case Keyboard.KEY_R: return Keymap.KEY_R;
            case Keyboard.KEY_S: return Keymap.KEY_S;
            case Keyboard.KEY_T: return Keymap.KEY_T;
            case Keyboard.KEY_U: return Keymap.KEY_U;
            case Keyboard.KEY_V: return Keymap.KEY_V;
            case Keyboard.KEY_W: return Keymap.KEY_W;
            case Keyboard.KEY_X: return Keymap.KEY_X;
            case Keyboard.KEY_Y: return Keymap.KEY_Y;
            case Keyboard.KEY_Z: return Keymap.KEY_Z;
            case Keyboard.KEY_0: return Keymap.KEY_0;
            case Keyboard.KEY_1: return Keymap.KEY_1;
            case Keyboard.KEY_2: return Keymap.KEY_2;
            case Keyboard.KEY_3: return Keymap.KEY_3;
            case Keyboard.KEY_4: return Keymap.KEY_4;
            case Keyboard.KEY_5: return Keymap.KEY_5;
            case Keyboard.KEY_6: return Keymap.KEY_6;
            case Keyboard.KEY_7: return Keymap.KEY_7;
            case Keyboard.KEY_8: return Keymap.KEY_8;
            case Keyboard.KEY_9: return Keymap.KEY_9;
            case Keyboard.KEY_NUMPAD0: return Keymap.KEY_0;
            case Keyboard.KEY_NUMPAD1: return Keymap.KEY_1;
            case Keyboard.KEY_NUMPAD2: return Keymap.KEY_2;
            case Keyboard.KEY_NUMPAD3: return Keymap.KEY_3;
            case Keyboard.KEY_NUMPAD4: return Keymap.KEY_4;
            case Keyboard.KEY_NUMPAD5: return Keymap.KEY_5;
            case Keyboard.KEY_NUMPAD6: return Keymap.KEY_6;
            case Keyboard.KEY_NUMPAD7: return Keymap.KEY_7;
            case Keyboard.KEY_NUMPAD8: return Keymap.KEY_8;
            case Keyboard.KEY_NUMPAD9: return Keymap.KEY_9;
            case Keyboard.KEY_SPACE: return Keymap.KEY_SPACE;
            case Keyboard.KEY_PERIOD: return Keymap.KEY_DOT;
            case Keyboard.KEY_BACK: return Keymap.KEY_BACK;
            case Keyboard.KEY_RETURN: return Keymap.KEY_RETURN;
            case Keyboard.KEY_NUMPADENTER: return Keymap.KEY_RETURN;
            case Keyboard.KEY_ESCAPE: return Keymap.KEY_ESC;
            case Keyboard.KEY_LEFT: return Keymap.KEY_LEFT;
            case Keyboard.KEY_RIGHT: return Keymap.KEY_RIGHT;
            case Keyboard.KEY_UP: return Keymap.KEY_UP;
            case Keyboard.KEY_DOWN: return Keymap.KEY_DOWN;
        }
        return 0;
    }
}
