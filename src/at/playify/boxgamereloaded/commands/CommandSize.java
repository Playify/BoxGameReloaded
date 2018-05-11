package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.util.Utils;

public class CommandSize extends Command {
    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (args.length!=2) {
            game.cmd.error("size <x> <y>");
            return;
        }
        int x=Utils.parseInt(args[0],game.level.sizeX);
        int y=Utils.parseInt(args[1],game.level.sizeY);
        Block[] blocks=game.level.blocks;
        int[] metas=game.level.metas;
        Block[] blk=new Block[x*y];
        int[] mta=new int[x*y];
        int yy=Math.min(y, game.level.sizeY);
        int xx=Math.min(x, game.level.sizeX);
        for (int i=0;i<yy;i++) {
            System.arraycopy(blocks,game.level.sizeX*i,blk,x*i,xx);
            System.arraycopy(metas,game.level.sizeX*i,mta,x*i,xx);
        }
        game.level.sizeX=x;
        game.level.sizeY=y;
        game.level.blocks=blk;
        game.level.metas=mta;
        game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
    }
}
