package at.playify.boxgamereloaded.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.Drawer;
import at.playify.boxgamereloaded.interfaces.exceptions.DrawingException;

import static javax.microedition.khronos.opengles.GL10.*;

@SuppressWarnings("WeakerAccess")
public class SurfaceDrawer implements Drawer {
    public int w;
    public int h;
    GL10 gl;
    public FontRenderer font;
    private boolean drawing;
    private GameActivity a;
    private BoxGameReloaded game;
    private FloatBuffer fb=null;
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
    public boolean ready;
    private float[] lineVertex=new float[]{0,0,0,1,1,1};

    public boolean ready(){
        return ready;
    }

    @Override
    public void drawLine(float x, float y, float z, float w, float h, float d, int color) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glScalef(w, h, d);
        vertexLine(lineVertex,color);
        gl.glPopMatrix();
    }

    private HashMap<String,Integer> textures=new HashMap<>();
    private FloatBuffer vertexBuffer,textureBuffer;
    {
        float vertices[] = new float[12];
        float texture[] = new float[8];

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();

        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();

        //Vertex
        vertexBuffer.position(0);
        //x
        vertices[0]=vertices[3]=0;
        vertices[6]=vertices[9]=1;
        //y
        vertices[1]=vertices[7]=0;
        vertices[4]=vertices[10]=1;
        //z
        vertices[2]=vertices[5]=vertices[8]=vertices[11]=0;
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        //Texture
        textureBuffer.position(0);
        texture[0]=texture[2]=0;
        texture[3]=texture[7]=0;
        texture[4]=texture[6]=1;
        texture[1]=texture[5]=1;

        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    @Override
    public void drawImage(String s) {
        if (!textures.containsKey(s)) {
            try {
                InputStream inStream = a.handler.asset("font.png");
                Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                int[] id=new int[1];
                gl.glGenTextures(1,id,0);
                gl.glBindTexture(GL_TEXTURE_2D,id[0]);
                gl.glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
                gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_NEAREST);
                gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,GL_REPEAT);
                gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,GL_REPEAT);
                GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);
                bitmap.recycle();
                textures.put(s,id[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        gl.glPushMatrix();
        gl.glEnable(GL_TEXTURE_2D);
        gl.glBindTexture(GL_TEXTURE_2D,textures.get(s));
        gl.glColor4f(1,1,1,1);

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        vertexBuffer.position(0);
        textureBuffer.position(0);

        gl.glVertexPointer(3,GL_FLOAT,0,vertexBuffer);
        gl.glTexCoordPointer(2,GL_FLOAT,0,textureBuffer);
        gl.glDrawArrays(GL_TRIANGLE_STRIP,0,4);

        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, 0);
        gl.glDisable(GL_TEXTURE_2D);

        gl.glPopMatrix();
    }


    SurfaceDrawer(GameActivity a, BoxGameReloaded game) {
        this.a=a;
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
        cube(x, y, z, w, h, d, color, true, true, true, true, true, back());
    }

    public void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right) {
        cube(x, y, z, w, h, d, color, up, left, down, right, true, back());
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

    @Override
    public void point(float x, float y, float z, int color) {
        point(x, y, z, color,3);
    }

    @Override
    public void point(float x, float y, float z, int color, float size) {
        FloatBuffer verticesBuffer=checkFloatBuffer(3);
        verticesBuffer.put(x);
        verticesBuffer.put(y);
        verticesBuffer.put(z);
        verticesBuffer.position(0);
        gl.glPointSize(size*h/1040f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color)&255)/255f;
        gl.glColor4f(r, g, b, a);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
        gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
        game.vertexcount++;
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
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
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color)&255)/255f;
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
        gl.glLineWidth(3*h/1040f);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color)&255)/255f;
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
            GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 10.0f, 2.0f);
            gl.glTranslatef(1, -1, 0);
            gl.glScalef(-2f, 2f, 2);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glDisable(GL10.GL_TEXTURE_2D);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_POINT_SMOOTH);
            drawing=true;
        }
    }

    @Override
    public void stopDrawing() {
        drawing=false;
    }

    @Override
    public void fill(int color) {
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color)&255)/255f;
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
        return game.vars.debug.viewback;
    }

    @Override
    public void drawString(String s, float x, float y, float h) {
        font.draw(s, x, y, h);
    }

    @Override
    public void drawStringCenter(String s, float x, float y, float h) {
        font.drawCenter(s, x, y, h);
    }

    @Override
    public float getStringWidth(String s) {
        return font.getWidth(s);
    }

    @Override
    public void back(boolean b) {
        game.vars.debug.viewback=b;
    }
}
