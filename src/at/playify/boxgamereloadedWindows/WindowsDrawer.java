package at.playify.boxgamereloadedWindows;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.exceptions.DrawingException;
import at.playify.boxgamereloaded.interfaces.Drawer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class WindowsDrawer implements Drawer {
    //Größe vom Fenster
    public int w;
    public int h;
    private FontRenderer font=new FontRenderer(this);
    private boolean drawing;//derzeitig am zeichnen
    private BoxGameReloaded game;
    //Vertexes um nicht jedes Mal zeichnen neu generieren zu müssen
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


    WindowsDrawer(BoxGameReloaded game) {
        this.game=game;
    }

    //Zeichenkoordinaten verschieben
    @Override
    public void translate(float x, float y) {
        GL11.glTranslatef(x, y, 0);
    }

    //Zeichenkoordinaten verschieben (auch Tiefe)
    @Override
    public void translate(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    //Zeichenbereich skalieren x=? y=? z=1(=bleibt)
    @Override
    public void scale(float x, float y) {
        GL11.glScalef(x, y, 1);
    }
    //Zeichenbereich skalieren x=? y=? z=?
    @Override
    public void scale(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    //Zeichenbereich skalieren x=y=z
    @Override
    public void scale(float v) {
        GL11.glScalef(v, v, v);
    }



    //Rotieren angle sind die Grad x,y,z sind Faktoren (meist 0,1,-1)
    @Override
    public void rotate(float angle, float x, float y, float z) {
        GL11.glRotatef(angle, x, y, z);
    }

    //Rechteck zeichnen bei den Koordinaten x,y mit der größe w,h mit der Farbe color
    //color erlaubt Alpha z.B. 0xFF00FF00 (grün)   ARGB Alpha Rot GGrün Blau
    @Override
    public void rect(float x, float y, float w, float h, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x,y,0);
        GL11.glScalef(w,h,0);
        vertex(vertexFRONT, color);
        GL11.glPopMatrix();
    }

    //Würfel zeichnen bei den Koordinaten x,y,z mit der größe w,h,d mit der Farbe color
    //color erlaubt Alpha z.B. 0xFF00FF00 (grün)   ARGB Alpha Rot GGrün Blau
    @Override
    public void cube(float x, float y, float z, float w, float h, float d, int color) {
        cube(x, y, z, w, h, d, color, true, true, true, true, true, back());
    }
    //Zeichne Würfel wie bei der Methode darüber, jedoch kann editiert werden welche Seiten gezeichnet werden
    @Override
    public void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right) {
        cube(x, y, z, w, h, d, color, up, left, down, right, true, back());
    }
    //Zeichne Würfel wie bei der Methode darüber, jedoch kann editiert werden welche Seiten (auch Vorder und Rückwand) gezeichnet werden
    @Override
    public void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right, boolean front, boolean back) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x,y,z);
        GL11.glScalef(w,h,d);


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
        GL11.glPopMatrix();
    }


    //Zeichne Vertex mit Farbe
    //Vertex besteht aus x1,y1,z1,x2,y2,z2,x3,y3,z3,...
    //Alle 9 Koordinaten in vertex zeichnen 1 Dreieck, es können mehrere Dreiecke in vertex übergeben werden
    @Override
    public void vertex(float[] vertex, int color) {
        vertex(vertex,color,1,1);
    }

    //darken ist ein Wert nahe 1 z.B. 0.9 macht color etwas dunkle, 1.1 macht color etwas heller
    @Override
    public void vertex(float[] vertex, int color, float darken) {
        vertex(vertex,color,darken,1);
    }

    //alpha multipliziert mit der Transparenz von color
    @Override
    public void vertex(float[] vertex, int color, float darken, float alpha) {
        game.vertexcount++;
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color)&255)/255f;
        GL11.glColor4f(r*darken, g*darken, b*darken, a*alpha);
        GL11.glBegin(GL11.GL_TRIANGLES);
        for (int i = 0; i < vertex.length; i+=3) {
            GL11.glColor4f(r*darken, g*darken, b*darken, a*alpha);
            GL11.glVertex3f(vertex[i],vertex[i+1],vertex[i+2]);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_VERTEX_ARRAY);
    }


    //Fensterbreite
    @Override
    public float getWidth() {
        return w;
    }

    //Fensterhöhe
    @Override
    public float getHeight() {
        return h;
    }


    //Zeichnen starten
    @Override
    public void startDrawing() throws DrawingException {
        if(drawing) {
            throw new DrawingException("Already drawing");
        } else {
            GL11.glViewport(0, 0, w, h);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            float v=3, b=1;
            GL11.glFrustum(-1/v, 1/v, -1/v, 1/v, b, 7);
            GL11.glClearColor(0, 0, 0, 1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GLU.gluLookAt( 0, 0, -3.05f, 0f, 0f, 0f, 0f, 10.0f, 2.0f);
            GL11.glTranslatef(1, -1, 0);
            GL11.glScalef(-2f, 2f, 2);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            drawing=true;
        }
    }

    //Zeichnen beenden, derzeit kein Nutzen, möglicherweise in der Zukunft
    @Override
    public void stopDrawing() {
        drawing=false;
    }

    //Fülle den ganzen Bildschirm in der Farbe color
    //Jeden Zeichenzyklus sollte dies Aufgerufen werden oder sonst ein Hintergrund gezeichnet werden da sonst der Hintergrund vom Letzten ausführen
    //genutzt wird
    @Override
    public void fill(int color) {
        float a=((color>>24)&255)/255f, r=((color>>16)&255)/255f, g=((color>>8)&255)/255f, b=((color)&255)/255f;
        GL11.glClearColor(r, g, b, a);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }

    //Derzeitige Zeichenposition,Rotation und Skalierung speichern
    @Override
    public void pushMatrix() {
        GL11.glPushMatrix();
    }

    //Letzte gespeicherte Position,Rotation und Skalierung laden
    @Override
    public void popMatrix() {
        GL11.glPopMatrix();
    }


    //Tiefeberechnung aktivieren
    //Wenn dies aktiv ist (standard) dann ist das sichtbar was näher ist
    //Wenn dies deaktiviert ist dann wird das letzte gezeichnete Objekt oben gezeichnet
    //Dies kann mitten während dem zeichnen umgeschalten werden um z.B. Objekte erzeugen zu können die eigentlich hinter etwas anderem währen trotzdem zu Zeichnen.
    @Override
    public void depth(boolean b) {
        if(b) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    //Tiefe löschen
    //Sinnvoll nach dem Spiel zeichnen ist es Tiefe zu löschen um den Gui immer über dem Level zeichnen zu können, depth(false) würde komisch ausschauen da ein Würfel
    //Seite für Seite gezeichnet wird und man eine Seite durch eine andere Seite durchsieht
    @Override
    public void clearDepth() {
        GL11.glClearDepth(1);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    //Breite festlegen
    @Override
    public void setWidth(int width) {
        w=width;
    }

    //Höhe festlegen
    @Override
    public void setHeight(int height) {
        h=height;
    }

    //ist Rückseite zeichnen eingeschalten?
    @Override
    public boolean back() {
        return game.vars.debug.drawback;
    }

    //Zeichne Text auf den Koordinaten x,y mit Höhe h
    @Override
    public void drawString(String s, float x, float y, float h) {
        font.draw(s, x, y, h);
    }

    //Zeichne Text zentriert auf den Koordinaten x,y mit Höhe h
    @Override
    public void drawStringCenter(String s, float x, float y, float h) {
        font.drawCenter(s, x, y, h);
    }
}
