package at.playify.boxgamereloaded.util;

public class Storage<B> {
    private Object[] arr=new Object[8];
    private final Object lock=new Object();
    private int size=0;

    public void add(B element){
        synchronized (lock) {
            if (size >= arr.length) {
                Object[] nw=new Object[arr.length*2];
                System.arraycopy(arr, 0, nw, 0, arr.length);
                arr=nw;
            }
            arr[size++]=element;
        }
    }

    public B poll(){
        if (size==0) {
            return null;
        } else {
            synchronized (lock) {
                return (B) arr[--size];
            }
        }
    }
}
