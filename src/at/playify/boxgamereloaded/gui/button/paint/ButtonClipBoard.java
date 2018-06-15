package at.playify.boxgamereloaded.gui.button.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
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
    public String genText() {
        return (variant?"Copy":"Paste")+" Level";
    }

    @Override
    public BoundingBox3d genBound() {
        if (variant) {
            buttonBound.set(game.aspectratio/2+0.025f, .4f, -.05f, game.aspectratio-.25f, .5f, 0);
        }else{
            buttonBound.set(game.aspectratio/2+0.025f, .25f, -.05f, game.aspectratio-.25f, .35f, 0);
        }
        return buttonBound;
    }

    @Override
    public boolean click(Finger finger) {
        try {
            if (variant) {
                game.handler.setClipboard(game.level.toWorldString());
                game.logger.show("Copied Level");
            } else {
                game.level.saveHistory();
                game.level.loadWorldString(game.handler.getClipboard());
                game.connection.sendPacket(new PacketLevelData(game.level.toWorldString()));
                game.connection.sendPacket(new PacketSpawn(game.level.spawnPoint));
                game.logger.show("Pasted Level");
            }
        }catch (Exception e){
            game.logger.error("Error "+(variant?"copying":"pasting")+" LevelData");
        }
        return true;
    }
}
