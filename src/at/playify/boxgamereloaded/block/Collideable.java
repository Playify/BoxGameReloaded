package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.player.PlayerSP;

//Interface für Blöcke die collidierbar sind
public interface Collideable {
    boolean onCollide(PlayerSP player);
}
