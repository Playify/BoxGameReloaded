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
        AIR = new BlockAir(game,BlockAir.chr);
        GROUND = new BlockGround(game,BlockGround.chr);
        BORDER = new BlockBorder(game,BlockBorder.chr);
        SPIKE=new BlockSpike(game,BlockSpike.chr);
        KILL=new BlockKill(game,BlockKill.chr);
        BLOCK08=new Block08(game,Block08.chr);
        LADDER=new BlockLadder(game,BlockLadder.chr);
        ONEWAY=new BlockOneWay(game,BlockOneWay.chr);
        BOOST=new BlockBoost(game,BlockBoost.chr);
        CHECK=new BlockCheckPoint(game,BlockCheckPoint.chr);
    }
}
