package at.playify.boxgamereloaded.level.compress;

public interface Compresser {
    String compress(LevelData data);
    LevelData decompress(String s);

    String version();
}
