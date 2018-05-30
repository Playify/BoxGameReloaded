package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.packet.PacketSkin;

public class CommandName extends Command {
    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        if (args.length==0) {
            game.logger.error("Your name is "+game.player.display);
        }else {
            StringBuilder str=new StringBuilder(args[0]);
            for (int i=1;i<args.length;i++) {
                str.append(" ").append(args[i]);
            }
            String name=str.toString();
            if (name.matches("[a-zA-Z0-9_. ]*")) {
                game.player.display=name;
                game.vars.loader.save();
                game.connection.sendPacketSoon(new PacketSkin(game.player));
                game.logger.show("Name changed to "+game.player.display);
            }else{
                game.logger.error("Error: Name contains illegal Character (Allowed Characters:a-Z 0-9 _ . [Space])");
            }
        }
    }
}
