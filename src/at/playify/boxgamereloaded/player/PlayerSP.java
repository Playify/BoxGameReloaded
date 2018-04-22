package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.block.Collideable;
import at.playify.boxgamereloaded.block.MultiCollideable;
import at.playify.boxgamereloaded.block.NoCollideable;
import at.playify.boxgamereloaded.network.packet.PacketMove;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.CollisionData;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class PlayerSP extends Player {
    private boolean jump;
    public int jumps;
    @SuppressWarnings("WeakerAccess")
    public boolean collidesVert, collidesHor;
    @SuppressWarnings("WeakerAccess")
    public boolean jumpKey, leftKey, rightKey;
    private ArrayList<Borrow.BorrowedCollisionData> arr=new ArrayList<>();

    public PlayerSP(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String skin() {
        if (display==null) {
            String s=((int) (Math.random()*1000))+"";
            s=("0000".substring(s.length()).concat(s)).substring(1);
            display="Player"+s;
        }
        return super.skin();
    }

    //Spielertick bewegung,...
    @Override
    public void tick() {
        //Wenn zeichnen dann nicht bewegen
        if (game.painter.pause()) {
            super.tick();
            return;
        }
        if (game.d!=null) {
            boolean left=false, right=false;
            //wenn nicht spiel pausiert ist
            if (!game.paused) {
                //Tasten abfragen
                left=game.keys['a'];
                right=game.keys['d'];
                boolean jump=game.keys['w']||game.keys['s']||game.keys[' '];
                float w=game.d.getWidth();
                for (Finger finger : game.fingers) {
                    if (finger.down&&!finger.control) {
                        float x=finger.getX()/w;
                        if (x<0.25) left=true;
                        else if (x<0.5) right=true;
                        else jump=true;
                    }
                }
                this.leftKey=left;
                this.rightKey=right;
                this.jumpKey=jump;
                //springen nur beim drücken nicht beim halten
                motionY/=((bound.w()+bound.h())/2)/0.8f;
                if (!game.vars.fly) {
                    if (this.jump!=jump) {
                        this.jump=jump;
                    } else jump=false;
                } else {
                    this.jump=jump;
                }
                //Springen
                if (jump&&jumps<game.vars.jumps) {
                    motionY=0.21f;
                    jumps++;
                }
            }

            //Spezialfälle für motionY
            if (game.vars.fly) {
                if (this.jump) {
                    motionY=.1f;
                } else {
                    motionY=-.1f;
                }
            } else {
                motionY-=0.01f;//Fallbeschleunigung
            }
            motionY*=((bound.w()+bound.h())/2)/0.8f;

            //motionX
            if (left!=right) {
                motionX=left ? -0.11f : 0.11f;
            } else {
                motionX=0;
                //motionX=Math.abs(this.motionX) <= 0.01f ? 0 : motionX * 0.75f;
            }
            if (game.vars.geometry_dash) {
                motionX=.11f;
            }
            motionX*=((bound.w()+bound.h())/2)/0.8f;

            //bewegen
            move(motionX, game.vars.inverted_gravity ? -motionY : motionY);


            //Spezialfähigkeiten von Blöcken mit Kollision ausführen
            Borrow.freeInside(arr);
            arr=game.level.collideList(bound, arr);
            ArrayList<Block> list=game.blocks.list;
            out:
            //noinspection ForLoopReplaceableByForEach
            for (int i1=0, listSize=list.size();i1<listSize;i1++) {
                Block block=list.get(i1);
                in:
                if (block instanceof Collideable) {
                    for (int i=0, arrSize=arr.size();i<arrSize;i++) {
                        CollisionData c=arr.get(i);
                        if (c.blk==block) {
                            if (((Collideable) block).onCollide(this, game.level, c.meta, arr)) {
                                break out;
                            } else if (!(block instanceof MultiCollideable)) {
                                break in;
                            }
                        }
                    }
                    if (block instanceof NoCollideable) {
                        ((NoCollideable) block).onNoCollide(this, game.level, arr);
                    }
                }
            }

        }
        super.tick();
        if (game.connection!=null) {//Server position updaten
            if (!game.connection.serverbound.equals(bound)) {
                game.connection.sendPacket(new PacketMove(bound));
                game.connection.serverbound.set(bound);
            }
        }
    }

    private void move(float moveX, float moveY) {
        float wantX=moveX;
        float wantY=moveY;
        synchronized (bound) {
            Borrow.BorrowedBoundingBox bound=Borrow.bound(this.bound.x(), this.bound.y(), this.bound.xw(), this.bound.yh());
            //Bound erweitern
            Borrow.BorrowedBoundingBox boundingBox=bound.addCoord(moveX, moveY);
            ArrayList<Borrow.BorrowedBoundingBox> list1=game.level.getCollisionBoxes(this, boundingBox);
            boundingBox.free();

            //Eigentliche Bewegung anhand der Kollisionsboxen begrenzen
            if (moveX!=0.0f) {
                int j5=0;

                for (int l5=list1.size();j5<l5;++j5) {
                    moveX=list1.get(j5).calculateXOffset(bound, moveX);
                }

                if (moveX!=0.0f) {
                    bound.offset(moveX, 0.0f);
                }
            }

            if (moveY!=0.0f) {
                int k5=0;

                for (int i6=list1.size();k5<i6;++k5) {
                    moveY=list1.get(k5).calculateYOffset(bound, moveY);
                }

                if (moveY!=0.0f) {
                    bound.offset(0.0f, moveY);
                } else {
                    jumps=0;
                }
            }
            //Nach Bewegung limitieren Koordinaten vom Collider zurück zu Bound setzen
            this.bound.set(bound.minX, bound.minY);

            Borrow.free(list1);
            bound.free();
        }
        collidesHor=wantX!=moveX;
        collidesVert=wantY!=moveY;

        if (wantX!=moveX) {
            motionX=0;
        }

        if (wantY!=moveY) {
            motionY=0;
        }
    }

    //Ausgeführt wenn Spieler von Block gekillt wird
    public void killedByBlock() {
        game.vars.check.die();
        motionX=motionY=0;
        game.vars.deaths++;
        game.vars.loader.save();
    }

    //Ausgeführt wenn Spieler von Respawnbutton gekillt wird
    public void killedByButton() {
        game.vars.check.die();
        motionX=motionY=0;
        game.vars.deaths++;
        game.vars.loader.save();
    }

    @Override
    public String name() {
        return game.vars.playerID;
    }

    @Override
    public String name(int data) {
        return "Player";
    }

    @Override
    public void paint(float x, float y, boolean click, Finger finger) {
        game.player.bound.setCenter(x, y);
        game.player.motionX=game.player.motionY=0;
    }
}
