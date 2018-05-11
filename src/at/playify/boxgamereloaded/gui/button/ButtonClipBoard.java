package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.network.packet.PacketLevelData;
import at.playify.boxgamereloaded.network.packet.PacketSpawn;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonClipBoard extends Button {
    private final boolean variant;

    public ButtonClipBoard(BoxGameReloaded game, boolean variant) {
        super(game);
        this.variant=variant;
    }

    @Override
    public String text() {
        return (variant?"Copy":"Paste")+" Level";
    }

    @Override
    public BoundingBox3d bound() {
        if (variant) {
            bound.set(.25f, .25f, -.05f, game.aspectratio/2-0.025f, .4f, 0);
        }else{
            bound.set(game.aspectratio/2+0.025f, .25f, -.05f, game.aspectratio-.25f, .4f, 0);
        }
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        try {
            if (variant) {
                game.handler.setClipboard(game.level.toWorldString());
                System.out.println("Copied Level");
            } else {
                game.level.loadWorldString(game.handler.getClipboard());
                game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
                game.connection.sendPacket(new PacketSpawn(game.level.spawnPoint));
                System.out.println("Pasted Level");
            }
        }catch (Exception e){
            System.err.println("Error "+(variant?"copying":"pasting")+" LevelData");
        }
        return true;
    }
}
