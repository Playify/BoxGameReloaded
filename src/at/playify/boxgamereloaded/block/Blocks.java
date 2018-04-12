package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;

import java.util.ArrayList;

public class Blocks {
    public Block[] map=new Block[Character.MAX_VALUE];
    public ArrayList<Block> list=new ArrayList<>();
    public BlockAir AIR;
    public BlockGround GROUND;
    public BlockBorder BORDER;
    public BlockSpike SPIKE;
    public BlockKill KILL;
    public Block08 BLOCK08;
    public BlockLadder LADDER;
    public BlockOneWay ONEWAY;
    public BlockBoost BOOST;
    public BlockCheckPoint CHECK;
    public int blockscount;

    //Block anhand des Zeichens bekommen
    public Block get(char c) {
        return map[c];
    }

    //Registriere Blöcke
    public void init(BoxGameReloaded game) {
        AIR = new BlockAir(game,'a');
        GROUND = new BlockGround(game,'g');
        BORDER = new BlockBorder(game,'b');
        SPIKE=new BlockSpike(game,'s');
        KILL=new BlockKill(game,'k');
        BLOCK08=new Block08(game,'o');
        LADDER=new BlockLadder(game,'l');
        ONEWAY=new BlockOneWay(game,'w');
        BOOST=new BlockBoost(game,'j');
        CHECK=new BlockCheckPoint(game,'c');
    }
}
