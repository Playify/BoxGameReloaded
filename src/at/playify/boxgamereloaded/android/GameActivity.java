package at.playify.boxgamereloaded.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.*;

import com.crashlytics.android.Crashlytics;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Game;
import io.fabric.sdk.android.Fabric;

public class GameActivity extends Activity {
    public Game game;
    private GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        game=new BoxGameReloaded(new AndroidHandler(this));
        GameView gameView=new GameView(this);
        setContentView(view=gameView);
        game.start();

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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
        int unicodeChar = event.getUnicodeChar();
        if (event.getAction()==KeyEvent.ACTION_DOWN)
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_ESCAPE:game.cheatCode('s');break;
                case KeyEvent.KEYCODE_P:game.cheatCode('b');break;
                case KeyEvent.KEYCODE_O:game.cheatCode('a');break;
                case KeyEvent.KEYCODE_VOLUME_UP:game.cheatCode('u');break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:game.cheatCode('d');break;
                case KeyEvent.KEYCODE_SPACE:game.cheatCode('u');break;
                case KeyEvent.KEYCODE_W:game.cheatCode('u');break;
                case KeyEvent.KEYCODE_A:game.cheatCode('l');break;
                case KeyEvent.KEYCODE_S:game.cheatCode('d');break;
                case KeyEvent.KEYCODE_D:game.cheatCode('r');break;
            }
        game.setKey(unicodeChar,true);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return unicodeChar!=0;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
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
        //boolean hasBack=KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (((BoxGameReloaded) game).drawer.draw) {
            ((BoxGameReloaded) game).options^=true;
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
