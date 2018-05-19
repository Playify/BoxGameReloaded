package at.playify.boxgamereloaded.block;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.Level;
import at.playify.boxgamereloaded.player.PlayerSP;
import at.playify.boxgamereloaded.util.Borrow;

import java.util.ArrayList;

public class BlockEasterEgg extends BlockDebug implements Collideable{
    public static final char chr='e';
    public BlockEasterEgg(BoxGameReloaded game, char c) {
        super(game, c);
    }

    @Override
    public String prefix() {
        return "E";
    }

    @Override
    public boolean onCollide(PlayerSP player, Level level, int meta, ArrayList<Borrow.BorrowedCollisionData> data) {
        boolean save=false;
        String egg=game.skin.egg[meta];
        if (!game.vars.eggs.contains(egg)) {
            game.vars.eggs.add(egg);
            save=true;
        }
        if (!egg.equals(game.player.skin)) {
            game.player.skin=egg;
            save=true;
        }
        if (save) {
            game.vars.loader.save();
        }
        return false;
    }

    @Override
    public int metaStates() {
        return game.skin.egg.length;
    }
}
