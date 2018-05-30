package at.playify.boxgamereloaded.gui;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;
import at.playify.boxgamereloaded.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class GuiChangelog extends Gui implements Comparator<String> {
    private float aspect;
    private ArrayList<String> lst=new ArrayList<>();
    private Scroller scroller;

    public GuiChangelog(BoxGameReloaded game) {
        super(game);
        scroller=new Scroller(game,this);
        scroller.factor=1/(.05f*.9f/.8f);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ButtonClose(game));
    }


    @Override
    public void draw() {
        scroller.scrollMax=lst.size()+1;
        scroller.draw();
        float scroll=scroller.getScroll();

        if (aspect!=game.aspectratio) {
            aspect=game.aspectratio;
            JSONObject json=game.handler.assetJson("changelog");
            ArrayList<String> keys=new ArrayList<>();
            Iterator<String> it=json.keys();
            while (it.hasNext()) {
                String next=it.next();
                keys.add(next);
            }
            Collections.sort(keys, this);
            TextCreator txt=new TextCreator(game, lst);
            try {
                for (String key : keys) {
                    if (!key.equals("TODO")) {
                        txt.add("$"+key+":");
                        JSONArray arr=json.getJSONArray(key);
                        for (int i=0;i<arr.length();i++) {
                            txt.add("-"+arr.getString(i));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        game.d.cube(game.aspectratio/4, .1f, 0.01f, game.aspectratio/2, .8f, .01f, 0xFF00FFFF);
        for (int i=(int) scroll-1;i<Math.min(lst.size(), scroll+9);i++) {
            if (i >= 0) {
                String s=lst.get(i);
                if (!s.isEmpty()) {
                    if (s.charAt(0)=='$') {
                        game.d.drawString(s.substring(1), game.aspectratio/4+.05f, .7f-((i+.75f)-scroll)*.05f*.9f/0.8f, .075f, 0x66000000);
                    } else {
                        if (s.charAt(0)=='-') {
                            game.d.drawString("-", game.aspectratio/4+.1f, .7f-((i)-scroll)*.05f*.9f/0.8f, .05f, 0x66000000);
                            s=s.substring(1);
                        }
                        game.d.drawString(s, game.aspectratio/4+.15f, .7f-(i-scroll)*.05f*.9f/0.8f, .05f, 0x66000000);
                    }
                }
            }
        }
        game.d.clearDepth();
        //blue overlay to hide text
        game.d.cube(game.aspectratio/4, .1f, 0.01f, game.aspectratio/2, .14f, .01f, 0xFF00FFFF, false, false, false, false);
        game.d.cube(game.aspectratio/4, .76f, 0.01f, game.aspectratio/2, .14f, .01f, 0xFF00FFFF, false, false, false, false);
        //white border
        game.d.cube(game.aspectratio/4, .76f, 0, game.aspectratio/2, .005f, .005f, 0xFFFFFFFF, true, false, true, false);
        game.d.cube(game.aspectratio/4, .235f, 0, game.aspectratio/2, .005f, .005f, 0xFFFFFFFF, true, false, true, false);
        game.d.cube(game.aspectratio*3/4-.005f, .235f, 0, .005f, .525f, .005f, 0xFFFFFFFF, false, true, false, true);
        game.d.cube(game.aspectratio/4, .235f, 0, .005f, .525f, .005f, 0xFFFFFFFF, false, true, false, true);
        game.d.drawStringCenter("Changelog", game.aspectratio/2, .8f, .08f, 0x66000000);
        super.draw();
    }

    @Override
    public boolean click(Finger finger) {
        return scroller.click(finger);
    }

    @Override
    public int compare(String s1, String s2) {
        String[] a1=s1.split("\\."), a2=s2.split("\\.");
        int ret=a2.length-a1.length;
        if (ret!=0) return ret;
        for (int i=0;i<a1.length;i++) {
            ret=-Integer.compare(Utils.parseInt(a1[i], -1), Utils.parseInt(a2[i], -1));
            if (ret!=0) return ret;
        }
        return s1.compareTo(s2);
    }


    private class ButtonClose extends Button {
        ButtonClose(BoxGameReloaded game) {
            super(game);
        }

        @Override
        public String text() {
            return "Close";
        }

        @Override
        public BoundingBox3d bound() {
            bound.set(game.aspectratio/4+.02f, .12f, 0, game.aspectratio/2-.01f, .22f, 0.01f);
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            game.gui.close(GuiChangelog.this);
            return true;
        }
    }

    @Override
    public boolean scroll(float f) {
        scroller.scroll(3*f);
        return true;
    }
}
