package at.playify.boxgamereloaded.actor;

import at.playify.boxgamereloaded.interfaces.Game;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

//Zuständig Text zu zeichnen
public class FontRenderer {
    private final WindowsDrawer d;

    private int textureID;
    //Alle Zeichen die in der Texture vorkommen
    private final String str = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
    private final int[] charWidth={6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 4, 4, 6, 7, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 1, 1, 1, 4, 2, 5, 6, 6, 6, 6, 3, 5, 5, 5, 6, 2, 6, 2, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 2, 2, 5, 6, 5, 6, 7, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 5, 6, 6, 2, 6, 5, 3, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 6, 6, 6, 6, 5, 2, 5, 7, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 3, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 4, 6, 6, 3, 6, 6, 6, 6, 6, 6, 6, 7, 6, 6, 6, 2, 6, 6, 8, 9, 9, 6, 6, 6, 8, 8, 6, 8, 8, 8, 8, 8, 6, 6, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 6, 9, 9, 9, 5, 9, 9, 8, 7, 7, 8, 7, 8, 8, 8, 7, 8, 8, 7, 9, 9, 6, 7, 7, 7, 7, 7, 9, 6, 7, 8, 7, 6, 6, 9, 7, 6, 7, 1};

    public FontRenderer(WindowsDrawer d) {
        //Texture Laden
        try {
            textureID=loadTexture(ImageIO.read(getClass().getResourceAsStream("/ascii.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.d=d;
    }

    //Zeichnet Text bei den Koordinaten x,y mit der Höhe von height
    public void draw(String s, float x, float y, float height){
        d.depth(false);
        glPushMatrix();
        glTranslatef(x,y,0);
        glScalef(height,height,1);

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D,textureID);
        glColor4f(0,0,0,.4f);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        float offset=0;
        for (char c : s.toCharArray()) {
            int i = str.indexOf(c);
            if(i==32){
                offset+=.5f;
            } else if (i!=-1){
                offset+=renderChar(i,offset);
            }
        }
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);

        glBindTexture(GL_TEXTURE_2D, 0);
        glDisable(GL_TEXTURE_2D);

        glPopMatrix();
        d.depth(true);
    }

    //Zeichnet Text Zentriert bei den Koordinaten x,y mit der Höhe von height
    public void drawCenter(String s, float x, float y, float h) {
        x-=getWidth(s)*h/2;
        draw(s, x, y, h);
    }


    //Zeichnet ein einzelnes Zeichen
    //ch ist ein index von str
    //offset ist das offset auf der X Achse
    private float renderChar(int ch,float offset){
        int i = ch % 16;
        int j = ch / 16;
        float width=charWidth[ch]/8f;

        float i16 = i / 16f;
        float j16 = j / 16f;

        glBegin(GL_QUADS);
        {
            glTexCoord2f(i16, j16+1/16f); glVertex3f(offset, 0, 0);
            glTexCoord2f(i16, j16); glVertex3f(offset, 1, 0);
            glTexCoord2f(i16 +1/16f*width, j16); glVertex3f(offset+width, 1, 0);
            glTexCoord2f(i16 +1/16f*width, j16+1/16f); glVertex3f(offset+width, 0, 0);
        }
        glEnd();
        return width;
    }

    //Zeichenbreite errechnen
    private float getWidth(String s) {
        int w=0;
        for (char c : s.toCharArray()) {
            int i = str.indexOf(c);
            if (i!=-1){
                w+=charWidth[c];//in font pixels
            }
        }
        return w/8f;
    }

    //Lade Texture in OpenGL aus BufferedImage
    private int loadTexture(BufferedImage image){
        try {
            //Bild zu Pixel[]
            int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

            //Pixel[] in einen Puffer speichern
            ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int pixel = pixels[y * image.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }

            buffer.flip();

            //Texture generieren und Parameter festlegen
            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glDisable(GL_TEXTURE_2D);

            //Puffer als Bild für die Texture verwenden
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            return textureID;
        }catch (Exception e){
            Game.report(e);
            return 0;
        }
    }
}
