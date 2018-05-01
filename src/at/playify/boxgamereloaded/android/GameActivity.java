package at.playify.boxgamereloaded.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.Toast;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiOverlay;
import at.playify.boxgamereloaded.interfaces.Game;

public class GameActivity extends Activity {
    public Game game;
    private GLSurfaceView view;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        game=new BoxGameReloaded(new AndroidHandler(this));
        setContentView(view=new GameView(this));
        game.start();

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return findViewById(android.R.id.content).dispatchTouchEvent(event);
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount()!=0) {
            return true;
        }
        System.out.println(event);
        int unicodeChar = event.getUnicodeChar();
        if (event.getAction()==KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    game.cheatCode('s');
                    break;
                case KeyEvent.KEYCODE_P:
                    game.cheatCode('b');
                    break;
                case KeyEvent.KEYCODE_O:
                    game.cheatCode('a');
                    break;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    game.cheatCode('u');
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    game.cheatCode('d');
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    game.cheatCode('u');
                    break;
                case KeyEvent.KEYCODE_W:
                    game.cheatCode('u');
                    break;
                case KeyEvent.KEYCODE_A:
                    game.cheatCode('l');
                    break;
                case KeyEvent.KEYCODE_S:
                    game.cheatCode('d');
                    break;
                case KeyEvent.KEYCODE_D:
                    game.cheatCode('r');
                    break;
            }
        }
        game.setKey(unicodeChar,event.getAction()==KeyEvent.ACTION_DOWN);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            game.cheatCode('s');

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return true;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
            return true;
        }
        return unicodeChar!=0;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getRepeatCount()!=0) {
            return true;
        }
        System.out.println(event);
        int unicodeChar = event.getUnicodeChar();
        game.setKey(unicodeChar,false);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            onBackPressed();
            return true;
        }
        return unicodeChar!=0;
    }

    @Override
    protected void onPause() {
        view.onPause();
        game.pause();
        super.onPause();
    }
    @Override
    protected void onResume() {
        view.onResume();
        game.resume();
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        game.cheatCode('s');
        if (((BoxGameReloaded) game).painter.draw) {
            GuiOverlay gui=((BoxGameReloaded) game).gui;
            if (gui.isOptionsVisible()) {
                gui.closeOptions();
            }else{
                gui.openOptions();
            }
            game.pauseLock.unlock();
        }else {
            game.paused ^= true;
            game.pauseLock.unlock();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
