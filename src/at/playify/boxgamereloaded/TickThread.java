package at.playify.boxgamereloaded;

import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.network.packet.PacketHello;

//TickThread ist zuständig um die Ticks vom Spiel zu bestimmen und zu berechnen
public class TickThread extends Thread{
    private BoxGameReloaded game;
    public long ticks;

    public TickThread(BoxGameReloaded game){
        this.game = game;
        setName("TickThread");
    }

    //Thread ausführen
    @Override
    public void run() {
        try {
            try {
                //game.connection=new ConnectionToServer(game, "192.168.0.123");
                game.connection=new EmptyConnection();
                game.connection.sendPacket(new PacketHello());
            }catch (Exception e){
                Game.report(e);
            }
            while (true) {
                tick();
            }
        }catch (Exception e){
            Game.report(e);
        }
    }


    //TickThread tick ausführen: Spieltick und gegebenfalls Locken
    private void tick() throws InterruptedException {
        try {
            if (!game.vars.tickOnDraw) {
                game.tick();
            }
            game.pauseLock.runlock();
        }catch (Exception e){
            Game.report(e);
        }
        try {
            if (game.vars.tickrate != 0) {
                Thread.sleep((long) (1000 / game.vars.tickrate));
            }
        }catch (Exception e){
            Game.report(e);
        }
    }
}
