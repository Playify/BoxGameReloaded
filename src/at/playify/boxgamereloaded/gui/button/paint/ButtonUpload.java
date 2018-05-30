package at.playify.boxgamereloaded.gui.button.paint;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.network.packet.PacketUploadLevel;
import at.playify.boxgamereloaded.util.Action;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonUpload extends Button {
    public ButtonUpload(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Upload";
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(game.aspectratio/2+0.025f, .55f, -.05f,game.aspectratio-.25f , .65f, 0);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        game.handler.keybd("Upload Name:", false,"", new Action.Bool<String>() {
            @Override
            public boolean exec(String s) {
                game.connection.sendPacketSoon(new PacketUploadLevel(s));
                return true;
            }
        });
        return true;
    }
}
