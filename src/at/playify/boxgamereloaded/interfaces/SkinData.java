package at.playify.boxgamereloaded.interfaces;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.util.BoundingBox3d;

import java.util.HashMap;

public class SkinData {
    private final BoxGameReloaded game;
    private final Skin def;
    private HashMap<String,Skin> map=new HashMap<>();

    public SkinData(BoxGameReloaded game) {
        this.game=game;
        map.put("cube", def=new SkinCube());
        map.put("border", new SkinBorder());
        map.put("bordercube", new SkinBorderCube());
        map.put("dot", new SkinDot());
        map.put("dotborder", new SkinDotBorder());
        map.put("ground", new SkinGround());
        map.put("dust", new SkinDust());
        map.put("5350", new Skin5350());
    }

    public Skin get(String s) {
        Skin skin=map.get(s);
        if (skin==null) {
            return def;
        } else {
            return skin;
        }
    }

    public interface Skin {
        void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2);
    }

    private class SkinCube implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            if (game.vars.cubic) {
                game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
    }

    private class SkinBorder implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            if (game.vars.cubic) {
                game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
    }

    private class SkinBorderCube implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            if (game.vars.cubic) {
                game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color2);
            } else {
                game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color2);
            }
            if (game.vars.cubic) {
                game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
            } else {
                game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
            }
        }
    }

    private class SkinDot implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            if (base) {
                if (game.vars.cubic) {
                    game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
                } else {
                    game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
                }
            }
            if (game.vars.cubic) {
                game.d.point(bound.minX, bound.minY, bound.minZ, color2);
                game.d.point(bound.minX, bound.maxY, bound.minZ, color2);
                game.d.point(bound.maxX, bound.minY, bound.minZ, color2);
                game.d.point(bound.maxX, bound.maxY, bound.minZ, color2);
                game.d.point(bound.minX, bound.maxY, bound.maxZ, color2);
                game.d.point(bound.minX, bound.minY, bound.maxZ, color2);
                game.d.point(bound.maxX, bound.maxY, bound.maxZ, color2);
                game.d.point(bound.maxX, bound.minY, bound.maxZ, color2);
            } else {
                game.d.point(bound.minX, bound.minY, 0, color2);
                game.d.point(bound.minX, bound.maxY, 0, color2);
                game.d.point(bound.maxX, bound.minY, 0, color2);
                game.d.point(bound.maxX, bound.maxY, 0, color2);
            }
        }
    }

    private class SkinDotBorder implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            if (base) {
                if (game.vars.cubic) {
                    game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
                } else {
                    game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
                }
            } else {
                if (game.vars.cubic) {
                    game.d.point(bound.minX, bound.minY, bound.minZ, color2);
                    game.d.point(bound.minX, bound.maxY, bound.minZ, color2);
                    game.d.point(bound.maxX, bound.minY, bound.minZ, color2);
                    game.d.point(bound.maxX, bound.maxY, bound.minZ, color2);
                    game.d.point(bound.minX, bound.maxY, bound.maxZ, color2);
                    game.d.point(bound.minX, bound.minY, bound.maxZ, color2);
                    game.d.point(bound.maxX, bound.maxY, bound.maxZ, color2);
                    game.d.point(bound.maxX, bound.minY, bound.maxZ, color2);
                } else {
                    game.d.point(bound.minX, bound.minY, 0, color2);
                    game.d.point(bound.minX, bound.maxY, 0, color2);
                    game.d.point(bound.maxX, bound.minY, 0, color2);
                    game.d.point(bound.maxX, bound.maxY, 0, color2);
                }
            }
        }
    }

    private class SkinGround implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            int x=((int) ((bound.minX+bound.maxX)/2));
            int y=((int) ((bound.minY+bound.maxY)/2));
            int z=((int) ((bound.minZ+bound.maxZ)/2));
            if (base) {
                if (game.vars.cubic) {
                    game.d.cube(x, y, z, 1, 1, 1, color);
                } else {
                    game.d.rect(x, y, 1, 1, color);
                }
            } else {
                if (game.vars.cubic) {
                    game.d.cube(x+.1f, y+.1f, z+.1f, .8f, .8f, .8f, color);
                } else {
                    game.d.rect(x+.1f, y+.1f, .8f, .8f, color);
                }
            }
        }
    }

    private class SkinDust implements Skin {
        @Override
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
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
        public void draw(Player p, BoundingBox3d bound, boolean base, int color, int color2) {
            float w=(bound.maxX-bound.minX)/2;
            float h=(bound.maxY-bound.minY)/2;
            float d=(bound.maxZ-bound.minZ);
            game.d.pushMatrix();
            game.d.translate(bound.minX+w, bound.minY+h, bound.minZ);
            game.d.scale(w, h, d);
            for (int i=0;i<4;i++) {//TODO efficientcy shading
                game.d.cube(-1/5f, 0, 0, 2/5f, 1, 1, color, false, true, false, true);
                game.d.cube(-1/5f, 3/5f, 0, 6/5f, 2/5f, 1, color, true, true, true, false);
                game.d.rotate(90, 0, 0, 1);
            }
            game.d.popMatrix();
        }
    }
}