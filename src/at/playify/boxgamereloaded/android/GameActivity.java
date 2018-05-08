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
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.GuiOverlay;
import at.playify.boxgamereloaded.interfaces.Game;

public class GameActivity extends Activity {
    public Game game;
    public AndroidHandler handler=new AndroidHandler(this);
    boolean doubleBackToExitPressedOnce=false;
    private GLSurfaceView view;
    private Handler osHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            checkUpdate();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    0xDEADC0DE);
        }
        game=new BoxGameReloaded(handler);
        setContentView(view=new GameView(this));
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
        System.out.println(event);
        int unicodeChar=event.getUnicodeChar();
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
        game.setKey(unicodeChar, event.getAction()==KeyEvent.ACTION_DOWN);
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
        if (requestCode==0xDEADC0DE&&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            checkUpdate();
        }
    }
}
