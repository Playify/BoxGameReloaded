package at.playify.boxgamereloaded;

import at.playify.boxgamereloaded.interfaces.Game;
import at.playify.boxgamereloaded.network.connection.ConnectionSinglePlayer;

//TickThread ist zuständig um die Ticks vom Spiel zu bestimmen und zu berechnen
public class TickThread extends Thread {
    private final BoxGameReloaded game;

    TickThread(BoxGameReloaded game) {
        this.game=game;
        setName("TickThread");
    }

    //Thread ausführen
    @Override
    public void run() {
        try {/*
            try {
                game.connection=new ConnectionToServer(game, "127.0.0.1");
                if (game.connection.isClosed()) {
                    game.connection.close();
                    game.connection=new ConnectionSinglePlayer(game);
                }
            } catch (Exception e) {
                Game.report(e);
            }*/
            game.connection=new ConnectionSinglePlayer(game);
            //noinspection InfiniteLoopStatement
            while (true) {
                tick();
            }
        } catch (Exception e) {
            Game.report(e);
        }
    }


    //TickThread tick ausführen: Spieltick und gegebenfalls Locken
    private void tick() {
        try {
            if (game.vars.tickOnDraw) {
                game.pauseLock.lock();
            } else {
                game.tick();
            }
            boolean locked=game.pauseLock.isLocked();
            game.pauseLock.runlock();
            if (locked) {
                game.lastticktime=System.currentTimeMillis();
            }
        } catch (Exception e) {
            Game.report(e);
        }
        try {
            if (game.vars.tickrate!=0) {
                Thread.sleep((long) (1000/game.vars.tickrate));
            }
        } catch (Exception e) {
            Game.report(e);
        }
    }
}
