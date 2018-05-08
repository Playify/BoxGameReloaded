package at.playify.boxgamereloaded.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static javax.microedition.khronos.opengles.GL10.*;

class FontRenderer {
    private final SurfaceDrawer d;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;

    private float vertices[] = new float[12];
    private float texture[] = new float[8];

    private final int[] textures=new int[1];
    private final String str = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    private final int[] charWidth={6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 6, 7, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6, 8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9, 8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1};

    FontRenderer(GameActivity a, SurfaceDrawer d) {
        InputStream inStream = a.game.handler.asset("ascii.png");
        Bitmap bitmap = BitmapFactory.decodeStream(inStream);
        GL10 gl = d.gl;
        gl.glGenTextures(1,textures,0);
        gl.glBindTexture(GL_TEXTURE_2D,textures[0]);
        gl.glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,GL_REPEAT);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,GL_REPEAT);
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0);
        bitmap.recycle();


        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
        gl.glDisable(GL_TEXTURE_2D);

        this.d=d;
    }
    public void draw(String s, float x, float y, float height){
        if (s==null)return;
        d.depth(false);
        GL10 gl = d.gl;
        gl.glPushMatrix();
        gl.glTranslatef(x,y,0);
        //noinspection SuspiciousNameCombination
        gl.glScalef(height,height,1);

        gl.glEnable(GL_TEXTURE_2D);
        gl.glBindTexture(GL_TEXTURE_2D,textures[0]);
        gl.glColor4f(0,0,0,.4f);

        gl.glEnableClientState(GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        float offset=0;
        for (char c : s.toCharArray()) {
            int i = str.indexOf(c);
            if(i==32){
                offset+=.5f;
            } else if (i!=-1){
                offset+=renderChar(gl,i,offset);
            }
        }
        gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL_VERTEX_ARRAY);

        gl.glBindTexture(GL_TEXTURE_2D, 0);
        gl.glDisable(GL_TEXTURE_2D);

        gl.glPopMatrix();
        d.depth(true);
    }

    private float renderChar(GL10 gl,int ch,float offset){
        int i = ch % 16;
        int j = ch / 16;
        float width=charWidth[ch]/8f;
        //Vertex
        vertexBuffer.position(0);
        //x
        vertices[0]=vertices[3]=offset;
        vertices[6]=vertices[9]=offset+width;
        //y
        vertices[1]=vertices[7]=0;
        vertices[4]=vertices[10]=1;
        //z
        vertices[2]=vertices[5]=vertices[8]=vertices[11]=0;
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        //Texture
        textureBuffer.position(0);
        texture[4]=texture[6]=(texture[0]=texture[2]=i/16f)+1/16f*width;
        texture[1]=texture[5]=(texture[3]=texture[7]=j/16f)+1/16f;

        textureBuffer.put(texture);
        textureBuffer.position(0);
        gl.glVertexPointer(3,GL_FLOAT,0,vertexBuffer);
        gl.glTexCoordPointer(2,GL_FLOAT,0,textureBuffer);
        gl.glDrawArrays(GL_TRIANGLE_STRIP,0,vertices.length/3);
        return width;
    }

    void drawCenter(String s, float x, float y, float h) {
        if (s==null)return;
        x-=getWidth(s)*h/2;
        draw(s, x, y, h);
    }

    float getWidth(String s) {
        if (s==null)return 0;
        int w=0;
        for (char c : s.toCharArray()) {
            int i = str.indexOf(c);
            if (i!=-1){
                w+=charWidth[c];//in font pixels
            }
        }
        return w/8f;
    }
}
