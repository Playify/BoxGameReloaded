package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionSinglePlayer;

public class CommandLan extends Command {
    @Override
    public boolean is(String cmd) {
        return cmd.equals("lan");
    }

    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (game.connection instanceof ConnectionSinglePlayer) {
            ConnectionSinglePlayer con=(ConnectionSinglePlayer) game.connection;
            if (con.isLanWorld()) {
                game.commandHandler.display("Lan World already open:"+game.handler.ip());
            } else {
                con.openLanWorld();
                game.commandHandler.display("Lan World opened:"+game.handler.ip());
            }
        } else {
            game.commandHandler.display("Can't open Lan World when connected to Server ("+game.connection.getIp()+")");
        }
    }
}
