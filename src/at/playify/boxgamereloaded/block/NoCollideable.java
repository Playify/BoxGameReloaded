package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;

public interface NoCollideable extends Collideable {
    void onNoCollide(PlayerSP player, Level level);
}
