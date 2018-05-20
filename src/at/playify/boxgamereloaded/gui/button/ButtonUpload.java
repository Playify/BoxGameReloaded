package at.playify.boxgamereloaded.gui.button;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

public class ButtonUpload extends Button {
    public ButtonUpload(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public String text() {
        return "Upload [WIP]";//TODO
    }

    @Override
    public BoundingBox3d bound() {
        bound.set(game.aspectratio/2+0.025f, .45f, -.05f,game.aspectratio-.25f , .55f, 0);
        return bound;
    }

    @Override
    public boolean click(Finger finger) {
        //game.connection.sendPacketSoon(new PacketUploadLevel());
        return true;
    }
}
