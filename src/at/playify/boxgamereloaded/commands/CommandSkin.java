package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;

public class CommandSkin extends Command {
    @Override
    public boolean is(String cmd) {
        return cmd.equals("skin")||cmd.equals("tail");
    }

    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (args.length==0) {
            game.commandHandler.error(cmd+" <value>");
            return;
        }
        if (cmd.equals("skin")) {
            if (game.skin.exist(args[0])) {
                game.player.skin=args[0];
                game.commandHandler.display("Skin set to "+args[0]);
            } else {
                game.commandHandler.error("Unknown Skin:"+args[0]);
            }
        } else if (cmd.equals("tail")) {
            if (game.tail.exist(args[0])) {
                game.player.tail=args[0];
                game.commandHandler.display("Tail set to "+args[0]);
            } else {
                game.commandHandler.error("Unknown Tail:"+args[0]);
            }
        }
    }
}
