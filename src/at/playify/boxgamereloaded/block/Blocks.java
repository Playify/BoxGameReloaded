package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;

import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Blocks {
    public final Block[] map=new Block[Character.MAX_VALUE];
    public final ArrayList<Block> list=new ArrayList<>();
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
    public BlockFinish FINISH;
    public BlockResize RESIZE;
    public BlockGravity GRAVITY;
    public BlockGravityPad GRAVITY_PAD;
    public BlockArrow ARROW;
    public BlockTeleporter TELEPORT;
    public BlockNear NEAR;
    public int blockscount;
    public BlockKey KEY;
    public BlockKeyhole KEYHOLE;
    public BlockAction ACTION;
    public BlockGlitch GLITCH;
    public BlockEasterEgg EGG;

    //Block anhand des Zeichens bekommen
    public Block get(char c) {
        return map[c];
    }

    //Registriere Blöcke
    public void init(BoxGameReloaded game) {
        AIR = new BlockAir(game,BlockAir.chr);
        GROUND = new BlockGround(game,BlockGround.chr);
        //BORDER = new BlockBorder(game,BlockBorder.chr);
        SPIKE=new BlockSpike(game,BlockSpike.chr);
        KILL=new BlockKill(game,BlockKill.chr);
        BLOCK08=new Block08(game,Block08.chr);
        LADDER=new BlockLadder(game,BlockLadder.chr);
        ONEWAY=new BlockOneWay(game,BlockOneWay.chr);
        BOOST=new BlockBoost(game,BlockBoost.chr);
        CHECK=new BlockCheckPoint(game,BlockCheckPoint.chr);
        FINISH=new BlockFinish(game,BlockFinish.chr);
        RESIZE = new BlockResize(game, BlockResize.chr);
        GRAVITY=new BlockGravity(game, BlockGravity.chr);
        GRAVITY_PAD=new BlockGravityPad(game, BlockGravityPad.chr);
        ARROW=new BlockArrow(game, BlockArrow.chr);
        TELEPORT=new BlockTeleporter(game, BlockTeleporter.chr);
        NEAR=new BlockNear(game, BlockNear.chr);
        KEY=new BlockKey(game, BlockKey.chr);
        KEYHOLE=new BlockKeyhole(game, BlockKeyhole.chr);
        ACTION=new BlockAction(game, BlockAction.chr);
        GLITCH=new BlockGlitch(game, BlockGlitch.chr);
        EGG=new BlockEasterEgg(game, BlockEasterEgg.chr);

    }
}
