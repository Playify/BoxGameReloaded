package at.playify.boxgamereloaded.player;

import at.playify.boxgamereloaded.BoxGameReloaded;

public class PlayerMP extends Player {
    private String name;
    public PlayerMP(BoxGameReloaded game,String s) {
        super(game);
        this.name=s;
    }

    @Override
    public String name() {
        return name;
    }
}
