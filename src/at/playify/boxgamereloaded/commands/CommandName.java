package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.packet.PacketSkin;

public class CommandName extends Command {
    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game){
        if (args.length==0) {
            game.logger.error("Your name is "+game.player.display);
        } else {
            StringBuilder str=new StringBuilder(args[0]);
            for (int i=1;i<args.length;i++) {
                str.append(" ").append(args[i]);
            }
            String name=str.toString();
            for (char c : name.toCharArray()) {
                if (!((c >= '0'&&c<='9')||(c >= 'a'&&c<='z')||(c >= 'A'&&c<='Z')||c=='_'||c=='.'||c==' ')) {
                    game.logger.error("Error: Name contains illegal Character '"+c+"' (Allowed Characters:a-Z 0-9 _ . [Space])");
                    return;
                }
            }
            game.player.display=name;
            game.vars.loader.save();
            game.connection.sendPacketSoon(new PacketSkin(game.player));
            game.logger.show("Name changed to "+game.player.display);
        }
    }
}
