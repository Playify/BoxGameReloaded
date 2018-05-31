package at.playify.boxgamereloaded.util;

public interface Action<T> {
    void exec(T t);

    interface Bool<T> {
        boolean exec(T t);
    }
}
