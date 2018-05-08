package at.playify.boxgamereloaded.util;

import java.util.concurrent.atomic.AtomicBoolean;

//Lock wird benutzt um den TickThread zu pausieren und wenn benötigt wieder zu aktivieren
@SuppressWarnings({"unused", "WeakerAccess"})
public class Lock {

    public AtomicBoolean locked=new AtomicBoolean();


    public Lock(boolean b) {
        locked.set(b);
    }

    public Lock() {
    }

    public void runlock() throws InterruptedException {
        while (locked.get()) {
            synchronized (lock) {
                lock.wait();
            }
        }
    }

    private final Object lock=new Object();

    public void lock() {
        synchronized (lock) {
            locked.set(true);
        }
    }

    public void unlock() {
        synchronized (lock) {
            locked.set(false);
            lock.notify();
        }
    }

    public boolean isLocked() {
        return locked.get();
    }
}
