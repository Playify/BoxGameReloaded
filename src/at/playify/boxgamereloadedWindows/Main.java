package at.playify.boxgamereloadedWindows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.Finger;
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

    public static void main(String[] args) {
        loadLibrary();
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

    //Librarys von Jar-Datei extrahieren um von LWJGL,OpenGL,... genutzt zu werden.
    private static void loadLibrary() {
        try {
            File base = new File(System.getenv("APPDATA"), "BoxGameReloaded");
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
