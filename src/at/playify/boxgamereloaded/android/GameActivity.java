package at.playify.boxgamereloaded.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiOverlay;
import at.playify.boxgamereloaded.interfaces.Keymap;

public class GameActivity extends Activity {
    public BoxGameReloaded game;
    public AndroidHandler handler=new AndroidHandler(this);
    boolean doubleBackToExitPressedOnce=false;
    public GLSurfaceView view;
    private Handler osHandler=new Handler();
    public RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            checkUpdate();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        game=new BoxGameReloaded(handler);
        layout=new RelativeLayout(this);
        layout.addView(view=new GameView(this));
        setContentView(layout);
        game.start();

        super.onCreate(savedInstanceState);
    }

    void checkUpdate() {

        new Thread() {
            @Override
            public void run() {
                URLConnection urlConnection;
                try {
                    urlConnection=new URL("http://playify.site90.net/bxgm/version").openConnection();
                    BufferedReader in=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuffer content=new StringBuffer();
                    while ((inputLine=in.readLine())!=null) {
                        content.append(inputLine);
                    }
                    in.close();
                    final String newest=content.toString();
                    final String own=handler.version();
                    System.out.println("Newest Version:"+newest+"Your Version:"+own);
                    if (newest.equals(own)) return;
                    osHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            final ContextThemeWrapper context;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                context=new ContextThemeWrapper(GameActivity.this, android.R.style.Theme_Material_Dialog);
                            }else{
                                context=GameActivity.this;
                            }
                            final LinearLayout ll=new LinearLayout(context);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            final TextView tv=new TextView(context);
                            ll.addView(tv);
                            tv.setText("There is an Update available!\n"+own+" -> "+newest);
                            final AlertDialog ad=new AlertDialog.Builder(context).setTitle("AutoUpdater").setView(ll)
                                    .setPositiveButton("OK", null).setNegativeButton("CANCEL", null).show();
                            ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
                                    ad.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(null);
                                    download();
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.logger.error("Unable to check for Updates:"+e.getClass().getSimpleName());
                }
            }
        }.start();
    }

    private void download() {
        Thread thread=new Thread() {
            @Override
            public void run() {
                try {
                    URL url=new URL("http://playify.site90.net/bxgm/download");
                    HttpURLConnection c=(HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();

                    File file=new File(new File(Environment.getExternalStorageDirectory(), "Download"), "BoxGameReloaded.apk");
                    if (file.exists()) {
                        file.delete();
                    }
                    FileOutputStream fos=new FileOutputStream(file);

                    InputStream is=c.getInputStream();

                    byte[] buffer=new byte[1024];
                    int len1;
                    while ((len1=is.read(buffer))!=-1) {
                        fos.write(buffer, 0, len1);
                    }
                    fos.close();
                    is.close();

                    Uri apkUri = FileProvider.getUriForFile(GameActivity.this,GameActivity.this.getPackageName() + ".provider",file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    GameActivity.this.startActivity(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setName("Downloader");
        thread.start();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            return findViewById(android.R.id.content).dispatchTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount()!=0) {
            return true;
        }
        int unicodeChar=event.getUnicodeChar();
        game.setKey(convert(event.getKeyCode()), event.getAction()==KeyEvent.ACTION_DOWN);
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            game.cheatCode('s');

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return true;
            }

            this.doubleBackToExitPressedOnce=true;
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

    private char convert(int keyCode) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_A: return Keymap.KEY_A;
                case KeyEvent.KEYCODE_B: return Keymap.KEY_B;
                case KeyEvent.KEYCODE_C: return Keymap.KEY_C;
                case KeyEvent.KEYCODE_D: return Keymap.KEY_D;
                case KeyEvent.KEYCODE_E: return Keymap.KEY_E;
                case KeyEvent.KEYCODE_F: return Keymap.KEY_F;
                case KeyEvent.KEYCODE_G: return Keymap.KEY_G;
                case KeyEvent.KEYCODE_H: return Keymap.KEY_H;
                case KeyEvent.KEYCODE_I: return Keymap.KEY_I;
                case KeyEvent.KEYCODE_J: return Keymap.KEY_J;
                case KeyEvent.KEYCODE_K: return Keymap.KEY_K;
                case KeyEvent.KEYCODE_L: return Keymap.KEY_L;
                case KeyEvent.KEYCODE_M: return Keymap.KEY_M;
                case KeyEvent.KEYCODE_N: return Keymap.KEY_N;
                case KeyEvent.KEYCODE_O: return Keymap.KEY_O;
                case KeyEvent.KEYCODE_P: return Keymap.KEY_P;
                case KeyEvent.KEYCODE_Q: return Keymap.KEY_Q;
                case KeyEvent.KEYCODE_R: return Keymap.KEY_R;
                case KeyEvent.KEYCODE_S: return Keymap.KEY_S;
                case KeyEvent.KEYCODE_T: return Keymap.KEY_T;
                case KeyEvent.KEYCODE_U: return Keymap.KEY_U;
                case KeyEvent.KEYCODE_V: return Keymap.KEY_V;
                case KeyEvent.KEYCODE_W: return Keymap.KEY_W;
                case KeyEvent.KEYCODE_X: return Keymap.KEY_X;
                case KeyEvent.KEYCODE_Y: return Keymap.KEY_Y;
                case KeyEvent.KEYCODE_Z: return Keymap.KEY_Z;
                case KeyEvent.KEYCODE_0: return Keymap.KEY_0;
                case KeyEvent.KEYCODE_1: return Keymap.KEY_1;
                case KeyEvent.KEYCODE_2: return Keymap.KEY_2;
                case KeyEvent.KEYCODE_3: return Keymap.KEY_3;
                case KeyEvent.KEYCODE_4: return Keymap.KEY_4;
                case KeyEvent.KEYCODE_5: return Keymap.KEY_5;
                case KeyEvent.KEYCODE_6: return Keymap.KEY_6;
                case KeyEvent.KEYCODE_7: return Keymap.KEY_7;
                case KeyEvent.KEYCODE_8: return Keymap.KEY_8;
                case KeyEvent.KEYCODE_9: return Keymap.KEY_9;
                case KeyEvent.KEYCODE_SPACE: return Keymap.KEY_SPACE;
                case KeyEvent.KEYCODE_PERIOD: return Keymap.KEY_DOT;
                case KeyEvent.KEYCODE_BACK: return Keymap.KEY_BACK;
                case KeyEvent.KEYCODE_ENTER: return Keymap.KEY_RETURN;
                case KeyEvent.KEYCODE_ESCAPE: return Keymap.KEY_ESC;
                case KeyEvent.KEYCODE_DPAD_LEFT: return Keymap.KEY_LEFT;
                case KeyEvent.KEYCODE_DPAD_RIGHT: return Keymap.KEY_RIGHT;
                case KeyEvent.KEYCODE_DPAD_UP: return Keymap.KEY_UP;
                case KeyEvent.KEYCODE_DPAD_DOWN: return Keymap.KEY_DOWN;
                case KeyEvent.KEYCODE_VOLUME_DOWN: return Keymap.KEY_VOL_DOWN;
                case KeyEvent.KEYCODE_VOLUME_UP: return Keymap.KEY_VOL_UP;
            }
            return 0;
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getRepeatCount()!=0) {
            return true;
        }
        System.out.println(event);
        int unicodeChar=event.getUnicodeChar();
        game.setKey(unicodeChar, false);
        if (keyCode==KeyEvent.KEYCODE_BACK&&event.isTracking()
                &&!event.isCanceled()) {
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
            } else {
                gui.openOptions();
            }
            game.pauseLock.unlock();
        } else {
            game.paused^=true;
            game.pauseLock.unlock();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            view.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            |View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            checkUpdate();
        }
    }
}
