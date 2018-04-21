package at.playify.boxgamereloaded.util;

//Lock wird benutzt um den TickThread zu pausieren und wenn benötigt wieder zu aktivieren
@SuppressWarnings({"unused", "WeakerAccess"})
public class Lock {


    public Lock(boolean b) {
        locked=b;
    }

    public Lock(){}

    public void runlock() throws InterruptedException{
        while (locked) {
            synchronized (lock) {
                lock.wait();
            }
        }
    }

    private final Object lock=new Object();
    private boolean locked=false;

    public void lock(){
        locked=true;
    }

    public void unlock(){
        if(locked) {
            locked = false;
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    public boolean isLocked() {
        return locked;
    }
}
