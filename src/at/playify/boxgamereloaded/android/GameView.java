package at.playify.boxgamereloaded.android;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import at.playify.boxgamereloaded.BoxGameReloaded;

public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer, View.OnTouchListener {
    private final GameActivity a;
    private SurfaceDrawer drawer;

    public GameView(GameActivity a) {
        super(a);
        drawer=new SurfaceDrawer((BoxGameReloaded) a.game);
        this.a=a;
        setEGLConfigChooser(8,8,8,8,16,0);
        setEGLContextClientVersion(1);
        setRenderer(this);
        setRenderMode(RENDERMODE_CONTINUOUSLY);

        setOnTouchListener(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        a.game.d=drawer;
        drawer.gl=gl;
        drawer.font=new FontRenderer(a,drawer);
        a.game.setCanDraw(true);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        drawer.gl=gl;
        drawer.w=width;
        drawer.h=height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        drawer.gl=gl;
        a.game.draw();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pointercount=event.getPointerCount();
        for (int i = 0; i < a.game.fingers.length; i++) {
            boolean d=a.game.fingers[i].down;
            if (i<pointercount) {
                a.game.fingers[i].x = event.getX(i);
                a.game.fingers[i].y = event.getY(i);
                a.game.fingers[i].down = event.getActionIndex() != i || (event.getActionMasked() != MotionEvent.ACTION_UP && event.getActionMasked() != MotionEvent.ACTION_POINTER_UP);
            }else {
                a.game.fingers[i].down=false;
            }
            if (a.game.fingers[i].down!=d){
                System.out.println("CHANGED");
                a.game.fingerStateChanged(a.game.fingers[i]);
            }
        }
        return true;
    }
}
