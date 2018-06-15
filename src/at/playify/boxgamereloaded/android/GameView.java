package at.playify.boxgamereloaded.android;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import at.playify.boxgamereloaded.BoxGameReloaded;

@SuppressLint("ViewConstructor")
public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer, View.OnTouchListener {
    private final GameActivity a;
    private SurfaceDrawer drawer;

    public GameView(GameActivity a) {
        super(a);
        a.game.d=drawer=new SurfaceDrawer(a,(BoxGameReloaded) a.game);
        this.a=a;
        setEGLConfigChooser(8,8,8,8,16,0);
        setEGLContextClientVersion(1);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        setOnTouchListener(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        drawer.gl=gl;
        drawer.font=new FontRenderer(a,drawer);
        drawer.ready=true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        drawer.gl=gl;
        drawer.w=width;
        drawer.h=height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            drawer.gl=gl;
            a.game.draw();
        }catch (Exception e){
            System.err.println("Drawing Error");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            if (event.getActionMasked()==MotionEvent.ACTION_DOWN){
                a.handler.setKeyboardVisible(false);
            }
            int pointercount=event.getPointerCount();
            for(int i=0; i<a.game.fingers.length; i++) {
                boolean d=a.game.fingers[i].down;
                if (i<pointercount) {
                    a.game.fingers[i].set(event.getX(i),event.getY(i));
                    a.game.fingers[i].down=event.getActionIndex()!=i||(event.getActionMasked()!=MotionEvent.ACTION_UP&&event.getActionMasked()!=MotionEvent.ACTION_POINTER_UP);
                } else {
                    a.game.fingers[i].down=false;
                }
                if (a.game.fingers[i].down!=d) {
                    a.game.fingerStateChanged(a.game.fingers[i]);
                }
            }
        }catch (Exception e){
            System.err.println("Input Excpetion");
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        a.osHandler.post(new Runnable() {
            @Override
            public void run() {
                a.layout();
            }
        });
    }
}
