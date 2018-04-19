package at.playify.boxgamereloaded.android;

import android.opengl.GLU;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.exceptions.DrawingException;
import at.playify.boxgamereloaded.interfaces.Drawer;

public class SurfaceDrawer implements Drawer {
    public int w;
    public int h;
    public GL10 gl;
    public FontRenderer font;
    private boolean drawing;
    private BoxGameReloaded game;
    public boolean back;//todo set if cube should draw back side
    private FloatBuffer fb=null;
    private FloatBuffer fb2=null;
    private float[] vertexFRONT=new float[]{
            0, 0, 0,
            0, 1, 0,
            1, 1, 0,
            0, 0, 0,
            1, 0, 0,
            1, 1, 0
    };
    private float[] vertexBACK=new float[]{
            0, 0, 1,
            0, 1, 1,
            1, 1, 1,
            0, 0, 1,
            1, 0, 1,
            1, 1, 1
    };
    private float[] vertexDOWN=new float[]{
            0, 0, 0,
            0, 0, 1,
            1, 0, 1,
            0, 0, 0,
            1, 0, 0,
            1, 0, 1
    };
    private float[] vertexUP=new float[]{
            0, 1, 0,
            0, 1, 1,
            1, 1, 1,
            0, 1, 0,
            1, 1, 0,
            1, 1, 1
    };
    private float[] vertexRIGHT=new float[]{
            0, 0, 0,
            0, 0, 1,
            0, 1, 1,
            0, 0, 0,
            0, 1, 0,
            0, 1, 1
    };
    private float[] vertexLEFT=new float[]{
            1, 0, 0,
            1, 0, 1,
            1, 1, 1,
            1, 0, 0,
            1, 1, 0,
            1, 1, 1
    };
    private float[] vertexLineRect=new float[]{
            0,0,0,
            0,1,0,
            0,1,0,
            1,1,0,
            1,1,0,
            1,0,0,
            1,0,0,
            0,0,0
    };
    private float[] vertexLineCube=new float[]{//Calculated from Windows edition
            0, 0, 0,
            0, 0, 1,
            0, 0, 0,
            0, 1, 0,
            0, 0, 0,
            1, 0, 0,
            0, 1, 0,
            0, 1, 1,
            0, 0, 1,
            0, 1, 1,
            0, 1, 0,
            1, 1, 0,
            1, 0, 0,
            1, 0, 1,
            1, 0, 0,
            1, 1, 0,
            0, 0, 1,
            1, 0, 1,
            1, 1, 0,
            1, 1, 1,
            1, 0, 1,
            1, 1, 1,
            0, 1, 1,
            1, 1, 1
    };


    public SurfaceDrawer(BoxGameReloaded game) {
        this.game=game;
    }

    @Override
    public void translate(float x, float y) {
        gl.glTranslatef(x, y, 0);
    }

    public void translate(float x, float y, float z) {
        gl.glTranslatef(x, y, z);
    }

    @Override
    public void scale(float x, float y) {
        gl.glScalef(x, y, 1);
    }
    public void scale(float x, float y,float z) {
        gl.glScalef(x, y, z);
    }

    public void scale(float v) {
        gl.glScalef(v, v, v);
    }

    @Override
    public void rect(float x, float y, float w, float h, int color) {
        gl.glPushMatrix();
        gl.glTranslatef(x,y,0);
        gl.glScalef(w,h,0);
        vertex(vertexFRONT, color);
        gl.glPopMatrix();
    }

    @Override
    public void lineRect(float x, float y, float w, float h, int color) {
        gl.glPushMatrix();
        gl.glTranslatef(x,y,0);
        gl.glScalef(w,h,1);
        vertexLine(vertexLineRect,color);
        gl.glPopMatrix();
    }

    public void cube(float x, float y, float z, float w, float h, float d, int color) {
        cube(x, y, z, w, h, d, color, true, true, true, true, true, back);
    }

    public void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right) {
        cube(x, y, z, w, h, d, color, up, left, down, right, true, back);
    }
    public void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right, boolean front, boolean back) {
        gl.glPushMatrix();
        gl.glTranslatef(x,y,z);
        gl.glScalef(w,h,d);


        //front
        if(front)
            vertex(vertexFRONT, color);
        //back
        if(back)
            vertex(vertexBACK, color);
        //down
        if(down)
            vertex(vertexDOWN, color, 0.8f);
        //up
        if(up)
            vertex(vertexUP, color, 0.8f);
        //right
        if(right)
            vertex(vertexRIGHT, color, 0.9f);
        //left
        if(left)
            vertex(vertexLEFT, color, 0.9f);
        gl.glPopMatrix();
    }

    @Override
    public void lineCube(float x, float y, float z, float w, float h, float d, int color) {
        gl.glPushMatrix();
        gl.glTranslatef(x,y,z);
        gl.glScalef(w,h,d);
        vertexLine(vertexLineCube,color);
        gl.glPopMatrix();
    }

    private FloatBuffer checkFloatBuffer(int length) {
        if(fb==null||fb.capacity()<length) {
            ByteBuffer vbb=ByteBuffer.allocateDirect(length*4);
            vbb.order(ByteOrder.nativeOrder());
            fb=vbb.asFloatBuffer();
        }
        fb.position(0);
        return fb;
    }
    private FloatBuffer checkFloatBuffer2(int length) {
        if(fb2==null||fb2.capacity()<length) {
            ByteBuffer vbb=ByteBuffer.allocateDirect(length*4);
            vbb.order(ByteOrder.nativeOrder());
            fb2=vbb.asFloatBuffer();
        }
        fb2.position(0);
        return fb2;
    }

    public void vertex(float[] vertex, int color) {
        vertex(vertex,color,1,1);
    }


    public void vertex(float[] vertex, int color, float darken) {
        vertex(vertex,color,darken,1);
    }

    public void vertex(float[] vertex, int color, float darken, float alpha) {
        FloatBuffer verticesBuffer=checkFloatBuffer(vertex.length);
        verticesBuffer.put(vertex);
        verticesBuffer.position(0);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color>>0)&255)/255f;
        gl.glColor4f(r*darken, g*darken, b*darken, a*alpha);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
        gl.glDrawArrays(GL10.GL_TRIANGLES,0, vertex.length/3);
        game.vertexcount+=vertex.length/3;
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void vertexLine(float[] vertex, int color) {
        FloatBuffer verticesBuffer=checkFloatBuffer(vertex.length);
        verticesBuffer.put(vertex);
        verticesBuffer.position(0);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color>>0)&255)/255f;
        gl.glColor4f(r, g, b, a);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
        gl.glDrawArrays(GL10.GL_LINES, 0, vertex.length/3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    @Override
    public float getWidth() {
        return w;
    }

    @Override
    public float getHeight() {
        return h;
    }


    @Override
    public void startDrawing() throws DrawingException {
        if(drawing) {
            throw new DrawingException("Already drawing");
        } else {
            gl.glViewport(0, 0, w, h);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            float v=3, b=1;
            gl.glFrustumf(-1/v, 1/v, -1/v, 1/v, b, 7);
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            GLU.gluLookAt(gl, 0, 0, -3.05f, 0f, 0f, 0f, 0f, 10.0f, 2.0f);
            gl.glTranslatef(1, -1, 0);
            gl.glScalef(-2f, 2f, 2);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glDisable(GL10.GL_TEXTURE_2D);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            drawing=true;
        }
    }

    @Override
    public void stopDrawing() {
        drawing=false;
    }

    @Override
    public void fill(int color) {
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color>>0)&255)/255f;
        gl.glClearColor(r, g, b, a);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
    }

    public void pushMatrix() {
        gl.glPushMatrix();
    }

    public void popMatrix() {
        gl.glPopMatrix();
    }

    public void depth(boolean b) {
        if(b) {
            gl.glEnable(GL10.GL_DEPTH_TEST);
        } else {
            gl.glDisable(GL10.GL_DEPTH_TEST);
        }
    }

    public void rotate(float angle, float x, float y, float z) {
        gl.glRotatef(angle, x, y, z);
    }

    public void clearDepth() {
        gl.glClearDepthf(1);
        gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void setWidth(int width) {
        w=width;
    }

    @Override
    public void setHeight(int height) {
        h=height;
    }

    @Override
    public boolean back() {
        return back;
    }

    @Override
    public void drawString(String s, float x, float y, float h) {
        font.draw(s, x, y, h);
    }

    @Override
    public void drawStringCenter(String s, float x, float y, float h) {
        font.drawCenter(s, x, y, h);
    }
}
