package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
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
        game.level.size(x,y);
        game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
    }
}
