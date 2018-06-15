package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.util.BoundingBox3d;

import java.util.HashMap;

public class SkinData {
    private final BoxGameReloaded game;
    private final Skin def;
    public String[] egg=new String[]{"cube","gute","rein","dave"};
    private HashMap<String,Skin> map=new HashMap<>();

    public SkinData(BoxGameReloaded game) {
        this.game=game;
        map.put("cube", def=new SkinCube());
        map.put("border", new SkinBorder());
        map.put("bordercube", new SkinBorderCube());
        map.put("ground", new SkinGround());
        map.put("dust", new SkinDust());
        map.put("5350", new Skin5350());
        for (int i=1;i<egg.length;i++) {
            map.put(egg[i], new SkinImage(egg[i]));
        }
    }

    public Skin get(String s) {
        Skin skin=map.get(s);
        if (skin==null) {
            return def;
        } else {
            return skin;
        }
    }

    public boolean exist(String s) {
        return map.containsKey(s);
    }

    public interface Skin {
        void draw(Player p, BoundingBox3d bound, int color, int color2);
    }

    private class SkinCube implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            if (game.vars.cubic) {
                game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
    }

    private class SkinBorder implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            if (game.vars.cubic) {
                game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
    }

    private class SkinBorderCube implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            if (game.vars.cubic) {
                game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color2);
                game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color2);
                game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
    }

    private class SkinGround implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            int x=((int) ((bound.minX+bound.maxX)/2));
            int y=((int) ((bound.minY+bound.maxY)/2));
            int z=((int) ((bound.minZ+bound.maxZ)/2));
                if (game.vars.cubic) {
                    game.d.cube(x, y, z, 1, 1, 1, color);
                } else {
                    game.d.rect(x, y, 1, 1, color);
                }
        }
    }

    private class SkinDust implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            float w=bound.maxX-bound.minX;
            float h=bound.maxY-bound.minY;
            float minZ=bound.minZ;
            float d=bound.maxZ-minZ;
            float v=w*h*d*200;
            if (!game.vars.cubic) d=minZ=0;
            for (int i=0;i<v;i++) {
                game.d.point((float) (bound.minX+w*Math.random()), (float) (bound.minY+h*Math.random()), (float) (minZ+d*Math.random()), color);
            }
        }
    }

    private class Skin5350 implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            float w=(bound.maxX-bound.minX);
            float h=(bound.maxY-bound.minY);
            float d=(bound.maxZ-bound.minZ);
            game.d.pushMatrix();
            game.d.translate(bound.minX, bound.minY, game.vars.cubic?bound.minZ:0);
            game.d.scale(w, h, d);
            game.d.scale(1/5f);
            if (game.vars.cubic) {
                game.d.cube(0, 0, 0, 3, 1, 5, color, true, false, true, true);
                game.d.cube(2, 4, 0, 3, 1, 5, color, true, true, true, false);
                game.d.cube(4, 0, 0, 1, 3, 5, color, false, true, true, true);
                game.d.cube(0, 2, 0, 1, 3, 5, color, true, true, false, true);
                game.d.cube(0, 2, 0, 5, 1, 5, color, true, false, true, false);
                game.d.cube(2, 0, 0, 1, 5, 5, color, false, true, false, true);
            }else{
                game.d.rect(0, 0,  3, 1, color);
                game.d.rect(2, 4,  3, 1, color);
                game.d.rect(4, 0,  1, 3, color);
                game.d.rect(0, 2,  1, 3, color);
                game.d.rect(0, 2,  5, 1, color);
                game.d.rect(2, 0,  1, 5, color);
            }
            game.d.popMatrix();
        }
    }

    private class SkinImage implements Skin {
        private String img;

        private SkinImage(String img) {
            this.img=img;
        }

        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {
            float w=(bound.maxX-bound.minX);
            float h=(bound.maxY-bound.minY);
            float d=(bound.maxZ-bound.minZ);
            game.d.pushMatrix();
            if (game.vars.cubic) {
                game.d.translate(bound.minX, bound.minY, bound.minZ);
                game.d.scale(w, h, d);
                for (int i=0;i<4;i++) {
                    game.d.translate(.5f, .5f, .5f);
                    game.d.rotate(90, 0, 1, 0);
                    game.d.translate(-.5f, -.5f, -.5f);
                    game.d.drawImage(img);
                }
                game.d.translate(.5f, .5f, .5f);
                game.d.rotate(90, 1, 0, 0);
                game.d.translate(-.5f, -.5f, -.5f);
                game.d.drawImage(img);
                game.d.translate(.5f, .5f, .5f);
                game.d.rotate(180, 1, 0, 0);
                game.d.rotate(180, 0, 0, 1);
                game.d.translate(-.5f, -.5f, -.5f);
                game.d.drawImage(img);
            }else{
                game.d.translate(bound.minX, bound.minY, 0);
                game.d.scale(w, h, 0);
                game.d.drawImage(img);
            }
            game.d.popMatrix();
        }
    }
}