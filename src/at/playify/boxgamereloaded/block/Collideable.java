package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;

import java.util.ArrayList;

//Interface für Blöcke die collidierbar sind
//Jeden Tick wird das Collide event nur einmal ausgeführt (außer der Block ist MultiCollideable)
public interface Collideable {
    boolean onCollide(PlayerSP player, Level level, int x, int y, int meta, ArrayList<Borrow.BorrowedCollisionData> data);
}
