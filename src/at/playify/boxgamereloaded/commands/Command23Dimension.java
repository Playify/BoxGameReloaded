package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;

public class Command23Dimension extends Command {
    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (cmd.equals("2d")) {
            game.vars.cubic=false;
        }else if (cmd.equals("3d")){
            game.vars.cubic=true;
        }else{
            System.err.println("How did you do that? Only \"2d\" and \"3d\" command can be executed here");
        }
        game.vars.loader.save();
    }
}
