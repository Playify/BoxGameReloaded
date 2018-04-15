package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.block.Collideable;
import at.playify.boxgamereloaded.block.MultiCollideable;
import at.playify.boxgamereloaded.network.packet.PacketMove;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.CollisionData;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class PlayerSP extends Player {
    private boolean jump;
    public int jumps;
    private ArrayList<Borrow.BorrowedCollisionData> arr = new ArrayList<>();
    @SuppressWarnings("WeakerAccess")
    public boolean collidesVert,collidesHor;
    @SuppressWarnings("WeakerAccess")
    public boolean jumpKey,leftKey,rightKey;

    public PlayerSP(BoxGameReloaded game) {
        super(game);
    }

    //Spielertick bewegung,...
    @Override
    public void tick() {
        //Wenn zeichnen dann nicht bewegen
        if (game.drawer.draw){
            super.tick();
            return;
        }
        if (game.d!=null) {
            boolean left=false,right=false;
            int side=game.vars.wallbounce ? side() : 0;
            //wenn nicht spiel pausiert ist
            if (!game.paused) {
                //Tasten abfragen
                left=game.keys['a'];
                right=game.keys['d'];
                boolean jump=game.keys['w']||game.keys['s']||game.keys[' '];
                float w=game.d.getWidth();
                for(Finger finger : game.fingers) {
                    if (finger.down&&!finger.control) {
                        float x=finger.x/w;
                        if (x<0.25) left=true;
                        else if (x<0.5) right=true;
                        else jump=true;
                    }
                }
                this.leftKey=left;
                this.rightKey=right;
                this.jumpKey=jump;
                //springen nur beim drücken nicht beim halten
                motionY /= ((bound.w() + bound.h()) / 2) / 0.8f;
                if (!game.vars.fly) {
                    if (this.jump!=jump) {
                        this.jump=jump;
                    } else jump=false;
                }else{
                    this.jump=jump;
                }
                //bei wallbounce sprünge zurücksetzen
                if (game.vars.wallbounce&&side!=0) {
                    jumps=0;
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
                    motionY = .1f;
                } else {
                    motionY = -.1f;
                }
            } else {
                motionY -= 0.01f;//Fallbeschleunigung
                if (game.vars.wallslide&&side!=0&&motionY<=-0.05f){
                    motionY=-0.05f;//Fallgeschwindigkeit limitieren bei wallslide
                }
            }
            motionY *= ((bound.w() + bound.h()) / 2) / 0.8f;

            //motionX
            if (left != right) {
                motionX = left ? -0.11f : 0.11f;
            }else {
                motionX=0;
                //motionX=Math.abs(this.motionX) <= 0.01f ? 0 : motionX * 0.75f;
            }
            if (game.vars.geometry_dash){
                motionX=.11f;
            }
            motionX *= ((bound.w() + bound.h()) / 2) / 0.8f;

            //bewegen
            move(motionX,game.vars.inverted_gravity?-motionY:motionY);


            //Spezialfähigkeiten von Blöcken mit Kollision ausführen
            Borrow.freeInside(arr);
            arr = game.level.collideList(collider.set(bound), this, arr);
            out:
            for(Block block : game.blocks.list) {
                in:
                if (block instanceof Collideable) {
                    for (CollisionData c : arr) {
                        if (c.blk == block) {
                            if (((Collideable) block).onCollide(this, game.level, c.x, c.y, c.meta, arr)) {
                                break out;
                            } else if (!(block instanceof MultiCollideable)) {
                                break in;
                            }
                        }
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
        float wantX = moveX;
        float wantY = moveY;
        synchronized (bound) {
            Borrow.BorrowedBoundingBox bound = Borrow.bound(this.bound.x(), this.bound.y(), this.bound.xw(), this.bound.yh());
            //Bound erweitern
            Borrow.BorrowedBoundingBox boundingBox = bound.addCoord(moveX, moveY);
            ArrayList<Borrow.BorrowedBoundingBox> list1 = game.level.getCollisionBoxes(this, boundingBox);
            boundingBox.free();

            //Eigentliche Bewegung anhand der Kollisionsboxen begrenzen
            if (moveX != 0.0f) {
                int j5 = 0;

                for (int l5 = list1.size(); j5 < l5; ++j5) {
                    moveX = list1.get(j5).calculateXOffset(bound, moveX);
                }

                if (moveX != 0.0f) {
                    bound.offset(moveX, 0.0f);
                }
            }

            if (moveY != 0.0f) {
                int k5 = 0;

                for (int i6 = list1.size(); k5 < i6; ++k5) {
                    moveY = list1.get(k5).calculateYOffset(bound, moveY);
                }

                if (moveY != 0.0f) {
                    bound.offset(0.0f, moveY);
                } else {
                    jumps = 0;
                }
            }
            //Nach Bewegung limitieren Koordinaten vom Collider zurück zu Bound setzen
            this.bound.set(bound.minX, bound.minY);

            Borrow.free(list1);
            Borrow.free(bound);
        }
        collidesHor = wantX != moveX;
        collidesVert=wantY != moveY;

        if (wantX != moveX)
        {
            motionX = 0;
        }

        if (wantY != moveY)
        {
            motionY = 0;
        }
    }

    //Ausgeführt wenn Spieler von Block gekillt wird
    public void killedByBlock() {
        this.bound.set(game.level.spawnPoint);
        motionX=motionY=0;
        game.vars.deaths++;
    }

    //Ausgeführt wenn Spieler von Respawnbutton gekillt wird
    public void killedByButton() {
        this.bound.set(game.level.spawnPoint);
        motionX=motionY=0;
        game.vars.deaths++;
    }

    @Override
    public String name() {
        return game.vars.playername;
    }
}
