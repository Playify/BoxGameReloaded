package at.playify.boxgamereloaded.gui.connect;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.Gui;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.network.connection.ConnectionSinglePlayer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiConnection extends Gui {
    public GuiConnection(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ButtonClose(game));
        buttons.add(new ButtonDisconnect(game));
        buttons.add(new ButtonLan(game));
    }

    @Override
    public void draw() {
        game.d.cube(game.aspectratio/4,.1f,0.01f,game.aspectratio/2,.8f,.01f,0xFF00FFFF);
        game.d.cube(game.aspectratio/4+.02f,.69f,0,game.aspectratio/2-.04f,.11f,.01f,0xFFAAAAAA);
        if (!(game.connection instanceof ConnectionSinglePlayer)){
            game.d.drawStringCenter("Connected to:",game.aspectratio/2,.8f,.08f, 0x66000000);
            game.d.drawStringCenter(game.connection.getIp(),game.aspectratio/2,.7f,.08f, 0x66000000);
        }else if (((ConnectionSinglePlayer) game.connection).isLanWorld()){
            game.d.drawStringCenter("Lan World:",game.aspectratio/2,.8f,.08f, 0x66000000);
            game.d.drawStringCenter(game.handler.ip(),game.aspectratio/2,.7f,.08f, 0x66000000);
        }else{
            game.d.drawStringCenter("SinglePlayer",game.aspectratio/2,.7f,.08f, 0x66000000);
        }
        game.d.cube(game.aspectratio*.45f+.01f,.57f,0,game.aspectratio*.3f-.03f,.1f,.01f,0xFFAAAAAA);
        if (game.connection.all!=null) {
            game.d.drawString("Connected:"+game.connection.all.size(),game.aspectratio*.45f+.03f,.595f,0.05f, 0x66000000);
        }
        super.draw();
    }

    @Override
    public boolean click(Finger finger) {
        super.click(finger);
        return true;
    }

    private class ButtonClose extends Button {
        public ButtonClose(BoxGameReloaded game) {
            super(game);
        }

        @Override
        public String text() {
            return "Close";
        }

        @Override
        public BoundingBox3d bound() {
            bound.set(game.aspectratio/4+.02f,.12f,0,game.aspectratio/2-.01f,.22f,0.01f);
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            game.gui.close(GuiConnection.this);
            return true;
        }
    }

    private class ButtonDisconnect extends Button {
        public ButtonDisconnect(BoxGameReloaded game) {
            super(game);
        }

        @Override
        public String text() {
            if (game.connection instanceof ConnectionSinglePlayer) {
                return "Connect";
            }else {
                return "Disconnect";
            }
        }

        @Override
        public BoundingBox3d bound() {
            bound.set(game.aspectratio/4+.02f,.24f,0,game.aspectratio*3/4-.02f,.44f,0.01f);
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            game.gui.close(GuiConnection.this);
            if (game.connection instanceof ConnectionSinglePlayer) {
                game.gui.openMenu(new GuiConnect(game));
            }else {
                game.connection.close();
                game.connection=new ConnectionSinglePlayer(game);
            }
            return true;
        }
    }

    private class ButtonLan extends Button {
        public ButtonLan(BoxGameReloaded game) {
            super(game);
        }

        @Override
        public String text() {
            if (game.connection instanceof ConnectionSinglePlayer) {
                return "Lan World";
            }else {
                return "";
            }
        }

        @Override
        public int color() {
            if (game.connection instanceof ConnectionSinglePlayer) {
                if (((ConnectionSinglePlayer) game.connection).isLanWorld()){
                    return 0xFF00FF00;
                }else {
                    return 0xFFFF0000;
                }
            }else {
                return 0;
            }
        }

        @Override
        public BoundingBox3d bound() {
            if (game.connection instanceof ConnectionSinglePlayer) {
                bound.set(game.aspectratio/4+.02f,.57f,0,game.aspectratio*.45f-.01f,.67f,0.01f);
            }else {
                bound.set(0,0,0,0,0,0);
            }
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            if (game.connection instanceof ConnectionSinglePlayer) {
                ConnectionSinglePlayer con=(ConnectionSinglePlayer) game.connection;
                if (con.isLanWorld()) {
                    con.closeLanWorld();
                }else{
                    con.openLanWorld();
                }
            }
            return true;
        }
    }
}
