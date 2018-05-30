package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
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
        game.level.shift(x,y);
        game.level.markDirty();
        game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
    }
}
