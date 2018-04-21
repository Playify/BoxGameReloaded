package at.playify.boxgamereloaded.level;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.block.Block;

public class FakeLevel extends Level {
    private Block blk;
    private int meta;

    public FakeLevel(BoxGameReloaded game) {
        super(game);
        super.setSize(1, 1);
        setBlock(game.blocks.AIR);
    }

    public void setBlock(Block block) {
        this.blk=block;
    }

    public void set(Block block, int meta) {
        this.blk=block;
        this.meta=meta;
    }

    public Block getBlock() {
        return blk;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta=meta;
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
    public void set(int x, int y, Block b) {
        if (x==0&y==0) {
            blk=b;
        }
    }

    @Override
    public int getMeta(int x, int y) {
        return x==0&&y==0 ? getMeta() : 0;
    }

    @Override
    public void set(int x, int y, Block b, int meta) {
        if (x==0&y==0) {
            blk=b;
            this.meta=meta;
        }
    }

    @Override
    public void setMeta(int x, int y, int meta) {
        if (x==0&y==0) {
            this.meta=meta;
        }
    }

    @Override
    public void setSize(int x, int y) {
    }

    @Override
    public void loadWorldString(String s) {
    }

    @Override
    public String toWorldString() {
        super.set(0, 0, blk, getMeta());
        return super.toWorldString();
    }
}
