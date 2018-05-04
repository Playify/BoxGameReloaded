package at.playify.boxgamereloaded.interfaces;

import java.util.HashMap;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.BoundingBox3d;

public class TailData {
    private final BoxGameReloaded game;
    private final TailData.Tail def;
    private HashMap<String,TailData.Tail> map=new HashMap<>();


    public TailData(BoxGameReloaded game) {
        this.game=game;
        map.put("false", def=new TailData.None());
        map.put("none", def);
        map.put("off0", def);
        map.put("true", new TailData.Normal());
        map.put("dot", new TailData.Dot());
        map.put("ground", new TailData.Ground());
        map.put("dust", new TailData.Dust());
        map.put("border", new TailData.Border());
        map.put("cube", new TailData.Cube());
        map.put("bordercube", new TailData.BorderCube());
    }

    public TailData.Tail get(String s) {
        TailData.Tail tail=map.get(s);
        if (tail==null) {
            return def;
        } else {
            return tail;
        }
    }

    public boolean exist(String s) {
        return map.containsKey(s);
    }

    public interface Tail {
        void draw(Player p, BoundingBox3d bound, int color, int color2);
    }

    private class None implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d bound, int color, int color2) {

        }
    }

    private class Normal implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            SkinData.Skin skin=game.skin.get(p.skin);
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
                } else {
                    skin.draw(p, bound, p.color, p.color2);
                }
            }
        }
    }

    private class Dot implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
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
    }

    private class Ground implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
                } else {
                    int x=((int) ((bound.minX+bound.maxX)/2));
                    int y=((int) ((bound.minY+bound.maxY)/2));
                    int z=((int) ((bound.minZ+bound.maxZ)/2));
                    if (game.vars.cubic) {
                        game.d.cube(x+.1f, y+.1f, z+.1f, .8f, .8f, .8f, color);
                    } else {
                        game.d.rect(x+.1f, y+.1f, .8f, .8f, color);
                    }
                }
            }
        }
    }

    private class Dust implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
                } else {
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
        }
    }

    private class Border implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
                } else {
                    if (game.vars.cubic) {
                        game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
                    } else {
                        game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
                    }
                }
            }
        }
    }

    private class Cube implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
                } else {
                    if (game.vars.cubic) {
                        game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
                    } else {
                        game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
                    }
                }
            }
        }
    }

    private class BorderCube implements Tail {
        @Override
        public void draw(Player p, BoundingBox3d drawbound, int color, int color2) {
            Borrow.BorrowedBoundingBox3d[] tailArray=p.tailArray;
            for (Borrow.BorrowedBoundingBox3d bound : tailArray) {
                if (bound==null) {
                    break;
                } else {
                    if (game.vars.cubic) {
                        game.d.lineCube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color2);
                        game.d.cube(bound.minX, bound.minY, bound.minZ, bound.maxX-bound.minX, bound.maxY-bound.minY, bound.maxZ-bound.minZ, color);
                    } else {
                        game.d.lineRect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color2);
                        game.d.rect(bound.minX, bound.minY, bound.maxX-bound.minX, bound.maxY-bound.minY, color);
                    }
                }
            }
        }
    }
}
