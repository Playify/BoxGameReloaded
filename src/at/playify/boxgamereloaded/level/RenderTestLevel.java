package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;

//Level um max. Renderleistung zu fordern
public class RenderTestLevel extends Level {
    public RenderTestLevel(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public Block get(int x, int y) {
        return game.blocks.BORDER;
    }
    @Override
    public Block get(int x, int y,Block def) {
        return game.blocks.BORDER;
    }

    @Override
    public int getMeta(int x, int y) {
        return 0;
    }
}
