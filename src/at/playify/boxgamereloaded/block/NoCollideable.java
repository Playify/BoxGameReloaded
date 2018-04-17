package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;

import java.util.ArrayList;

public interface NoCollideable extends Collideable {
    void onNoCollide(PlayerSP player, Level level, ArrayList<Borrow.BorrowedCollisionData> data);
}
