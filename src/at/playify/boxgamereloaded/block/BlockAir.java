package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.Player;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;
import at.playify.boxgamereloaded.util.bound.Bound;

import java.util.ArrayList;

//Luft
public class BlockAir extends Block {
    public BlockAir(BoxGameReloaded game, char c) {
        super(game,c);
    }

    @Override
    public boolean collide(Bound b, int x, int y, Player player, boolean checkOnly, int meta, Level level) {
        return false;
    }

    @Override
    public void draw(int x, int y, Level level) {

    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void getCollisionBox(Level level, int x, int y, Borrow.BorrowedBoundingBox bound, ArrayList<Borrow.BorrowedBoundingBox> list, PlayerSP player) {

    }
}