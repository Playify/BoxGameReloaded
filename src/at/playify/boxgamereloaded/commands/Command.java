package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;

public abstract class Command {

    public abstract void run(String cmd, String[] args, BoxGameReloaded game);
}
