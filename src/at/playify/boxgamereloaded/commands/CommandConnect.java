package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.connection.ConnectionSinglePlayer;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.packet.PacketHello;
import at.playify.boxgamereloaded.network.packet.PacketSetWorld;

public class CommandConnect extends Command {
    @Override
    public boolean is(String cmd) {
        return cmd.equals("connect");
    }

    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (args.length==0) {
            game.commandHandler.error("connect <to/get/stop/lan>");
            return;
        }
        if (args[0].equals("to")) {
            if (args.length==1) {
                game.commandHandler.error("connect to <ip>");
                return;
            }
            try {
                ConnectionToServer con=new ConnectionToServer(game, args[1]);
                if (con.isClosed()) {
                    game.commandHandler.error("Error connecting to:"+args[1]);
                    return;
                }
                game.connection.close();
                game.connection=con;
                game.connection.sendPacket(new PacketHello());
                game.connection.sendPacket(new PacketSetWorld(game.vars.world));
                game.commandHandler.display("Connected to "+con.getIp());
            } catch (Exception e) {
                e.printStackTrace();
                game.commandHandler.error("Error connecting to:"+args[1]);
            }
        } else if (args[0].equals("get")) {
            if (game.connection instanceof ConnectionSinglePlayer) {
                game.commandHandler.display("Connected to SinglePlayer:"+game.handler.ip());
            } else {
                game.commandHandler.display("Connected to "+game.connection.getIp());
            }
        } else if (args[0].equals("stop")) {
            game.connection.close();
            game.connection=new ConnectionSinglePlayer(game);
            game.connection.sendPacket(new PacketHello());
            game.connection.sendPacket(new PacketSetWorld(game.vars.world));
            game.commandHandler.display("Disconnected");
        } else if (args[0].equals("lan")) {
            if (game.connection instanceof ConnectionSinglePlayer) {
                ConnectionSinglePlayer con=(ConnectionSinglePlayer) game.connection;
                if (con.isLanWorld()) {
                    game.commandHandler.display("Lan World already open:"+game.handler.ip());
                } else {
                    con.openLanWorld();
                    game.commandHandler.display("Lan World opened:"+game.handler.ip());
                }
            } else {
                game.commandHandler.display("Can't open Lan World when connected to Server!");
            }
        } else {
            game.commandHandler.error("connect <to/get/stop/lan>");
        }
    }
}
