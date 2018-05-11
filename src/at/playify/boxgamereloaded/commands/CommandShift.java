package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.util.Utils;

public class CommandShift extends Command {
    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (args.length!=2) {
            game.cmd.error("shift <x> <y>");
            return;
        }
        int x=-Utils.parseInt(args[0],0);
        int y=-Utils.parseInt(args[1],0);
        x=((x%game.level.sizeX)+game.level.sizeX)%game.level.sizeX;
        y=((y%game.level.sizeY)+game.level.sizeY)%game.level.sizeY;
        Block[] blocks=game.level.blocks;
        int[] metas=game.level.metas;
        Block[] blk=new Block[blocks.length];
        int[] mta=new int[metas.length];
        for (int i=0;i<blocks.length;i++) {
            int ii=(i+x)%(blocks.length);
            if (x!=0&&ii%game.level.sizeX<x) {
                ii+=blocks.length-game.level.sizeX;
            }
            ii+=y*game.level.sizeX;
            blk[i]=blocks[ii%blocks.length];
            mta[i]=metas[ii%blocks.length];
        }
        game.level.blocks=blk;
        game.level.metas=mta;
        game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
    }
}
