package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;

public class FakeLevel extends Level {
    private Block blk;
    private int meta;

    public FakeLevel(BoxGameReloaded game) {
        super(game);
        super.setSize(1, 1);
        set(game.blocks.AIR, 0);
    }

    private void set(Block block, int meta) {
        this.blk=block;
        this.meta=meta;
    }

    public Block getBlock() {
        return blk;
    }

    public int getMeta() {
        return meta;
    }

    @Override
    public Block get(int x, int y) {
        if (x==0&y==0) return blk;
        else return game.blocks.AIR;
    }

    @Override
    public Block get(int x, int y, Block def) {
        if (x==0&y==0) return blk;
        else return game.blocks.AIR;
    }

    @Override
    public boolean set(int x, int y, Block b) {
        if (x==0&y==0) {
            blk=b;
            return true;
        }
        return false;
    }

    @Override
    public int getMeta(int x, int y) {
        return x==0&&y==0 ? meta : 0;
    }

    @Override
    public boolean set(int x, int y, Block b, int meta) {
        if (x==0&y==0) {
            blk=b;
            this.meta=meta;
            return true;
        }
        return false;
    }

    @Override
    public boolean setMeta(int x, int y, int meta) {
        if (x==0&y==0) {
            this.meta=meta;
            return true;
        }
        return false;
    }

    @Override
    public void setSize(int x, int y) {
    }

    @Override
    public void loadWorldString(String s) {
    }

    @Override
    public String toWorldString() {
        super.set(0, 0, blk, meta);
        return super.toWorldString();
    }
}
