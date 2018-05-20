package at.playify.boxgamereloaded.gui.connect;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.gui.Gui;
import at.playify.boxgamereloaded.gui.button.Button;
import at.playify.boxgamereloaded.network.connection.ConnectionSinglePlayer;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.util.BoundingBox3d;
import at.playify.boxgamereloaded.util.Finger;

import java.util.ArrayList;

public class GuiConnect extends Gui {
    private String ip="";

    public GuiConnect(BoxGameReloaded game) {
        super(game);
    }

    @Override
    public void initGui(ArrayList<Button> buttons) {
        buttons.add(new ButtonConnect(game));
        buttons.add(new ButtonClose(game));
        char[] keys="123456789.0\b".toCharArray();
        for (int i=0;i<12;i++) {
            buttons.add(new ButtonKey(game,keys[i],i));
        }
    }

    @Override
    public void draw() {
        game.d.cube(game.aspectratio/4,.1f,0.01f,game.aspectratio/2,.8f,.01f,0xFF00FFFF);
        game.d.cube(game.aspectratio/4+.02f,.69f,0,game.aspectratio/2-.04f,.11f,.01f,0xFFAAAAAA);
        game.d.drawStringCenter(ip,game.aspectratio/2,.7f,.08f, 0x66000000);
        super.draw();
    }

    @Override
    public boolean click(Finger finger) {
        super.click(finger);
        return true;
    }

    @Override
    public boolean key(char c, boolean down) {
        if (down&&valid(c)){
            if (c=='\b') {
                if (!ip.isEmpty()) {
                    ip=ip.substring(0, ip.length()-1);
                }
            } else {
                ip+=c;
            }
        }
        if (down&&c=='\n'){
            new Thread(){
                @Override
                public void run() {
                    connect();
                }
            }.start();
        }
        return true;
    }

    private void connect() {
        Thread thread=new Thread() {
            @Override
            public void run() {
                try {
                    ConnectionToServer con=new ConnectionToServer(game, ip);
                    if (con.isClosed()) {
                        game.cmd.error("Error connecting to:"+ip);
                        return;
                    }
                    game.connection.close();
                    game.connection=con;
                }catch (Exception e){
                    game.connection.close();
                    game.connection=new ConnectionSinglePlayer(game);
                }
            }
        };
        thread.setName("Connecter");
        thread.start();
    }

    private class ButtonConnect extends Button {
        public ButtonConnect(BoxGameReloaded game) {
            super(game);
        }

        @Override
        public String text() {
            return "OK";
        }

        @Override
        public BoundingBox3d bound() {
            bound.set(game.aspectratio/2+.01f,.12f,0,game.aspectratio*3/4-.02f,.22f,0.01f);
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            connect();
            game.gui.close(GuiConnect.this);
            game.gui.openMenu(new GuiConnection(game));
            return true;
        }
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
            game.gui.close(GuiConnect.this);
            game.gui.openMenu(new GuiConnection(game));
            return true;
        }
    }

    private class ButtonKey extends Button {
        private final char key;
        private final int index;

        public ButtonKey(BoxGameReloaded game, char key,int index) {
            super(game);
            this.index=index;
            this.key=key;
        }

        @Override
        public String text() {
            return key=='\b'?"<":key+"";
        }

        @Override
        public int color() {
            return valid(key)?0xFF00FF00:0xFFFF0000;
        }

        @Override
        public BoundingBox3d bound() {
            float x=index%3;
            float y=3-index/3;
            float h=.1f;
            x*=h;
            y*=h;
            x+=game.aspectratio/2;
            y+=.5f;
            float v=.01f;
            x-=h*1.5f;
            y-=.25f;
            bound.set(x+v,y+v,0,x+h-v,y+h-v,.01f);
            return bound;
        }

        @Override
        public boolean click(Finger finger) {
            if (valid(key)) {
                if (key=='\b') {
                    if (!ip.isEmpty()) {
                        ip=ip.substring(0, ip.length()-1);
                    }
                } else {
                    ip+=key;
                }
            }
            return true;
        }
    }

    private boolean valid(char key) {
        if (key!='.'&&key!='\b'&&!Character.isDigit(key))return false;
        char lst=ip.isEmpty()?'.':ip.charAt(ip.length()-1);
        char lst2=ip.length()<2?'.':ip.charAt(ip.length()-2);
        char lst4=ip.length()<4?ip.length()==3?'.':'\0':ip.charAt(ip.length()-4);
        if (ip.isEmpty()&&(key=='.'||key=='0'||key=='\b'))return false;
        if (key=='.'){
            int count=0;
            for (char c : ip.toCharArray()) {
                if (c=='.') {
                    if (++count>2) {
                        return false;
                    }
                }
            }
            return lst!='.';
        }
        if (key=='\b')return true;
        //NUMBERS
        if (lst=='.')return true;
        if (lst2=='.')return lst!='0';
        if (lst4=='.')return false;
        //lst char of block
        if (lst2>'2'||(lst2=='2'&&lst>'5'))return false;
        if (lst2=='2'&&lst=='5')return key<'6';
        return true;
    }
}
