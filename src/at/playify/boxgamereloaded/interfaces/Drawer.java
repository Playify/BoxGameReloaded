package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.interfaces.exceptions.DrawingException;

//Interface für Zeichner, unterschiedlich für jede Platform
@SuppressWarnings("SameParameterValue")
public interface Drawer {

    float getWidth();

    void setWidth(int width);

    float getHeight();

    void setHeight(int height);


    void back(boolean b);

    boolean back();

    void translate(float x, float y);

    void translate(float x, float y, float z);

    void scale(float x, float y);

    void scale(float x, float y, float z);

    void scale(float v);

    void rotate(float angle, float x, float y, float z);

    void rect(float x, float y, float w, float h, int color);

    void lineRect(float x, float y, float w, float h, int color);

    void cube(float x, float y, float z, float w, float h, float d, int color);

    void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right);

    //Zeichne Würfel wie bei der Methode darüber, jedoch kann editiert werden welche Seiten (auch Vorder und Rückwand) gezeichnet werden
    void cube(float x, float y, float z, float w, float h, float d, int color, boolean up, boolean left, boolean down, boolean right, boolean front, boolean back);

    void lineCube(float x, float y, float z, float w, float h, float d, int color);

    void point(float x, float y, float z, int color);

    void vertex(float[] vertex, int color);

    void vertex(float[] vertex, int color, float darken);

    void vertex(float[] vertex, int color, float darken, float alpha);

    void drawString(String s, float x, float y, float h);

    void drawStringCenter(String s, float x, float y, float h);

    void startDrawing() throws DrawingException;

    void stopDrawing();

    void fill(int color);

    void pushMatrix();

    void popMatrix();

    void depth(boolean b);


    void clearDepth();


}
