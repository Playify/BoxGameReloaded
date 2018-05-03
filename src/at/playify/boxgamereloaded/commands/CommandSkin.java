package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;

public class CommandSkin extends Command {

    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (args.length==0) {
            game.cmd.error(cmd+" <value>");
            return;
        }
        if (cmd.equals("skin")) {
            if (game.skin.exist(args[0])) {
                game.player.skin=args[0];
                game.cmd.display("Skin set to "+args[0]);
            } else {
                game.cmd.error("Unknown Skin:"+args[0]);
            }
        } else if (cmd.equals("tail")) {
            if (game.tail.exist(args[0])) {
                game.player.tail=args[0];
                game.cmd.display("Tail set to "+args[0]);
            } else {
                game.cmd.error("Unknown Tail:"+args[0]);
            }
        }
    }
}
