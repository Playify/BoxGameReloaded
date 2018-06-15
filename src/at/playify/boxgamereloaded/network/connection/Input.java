package at.playify.boxgamereloaded.network.connection;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Input implements Closeable {
    private DataInputStream in;

    public Input(InputStream s){
        in=new DataInputStream(s);
    }

    public String readString() throws IOException{
        int length=in.readInt();
        char[] chars=new char[length];
        for (int i=0;i<length;i++) {
            chars[i]=in.readChar();
        }
        return new String(chars);
    }

    public int readInt() throws IOException{
        return in.readInt();
    }
    public char readChar() throws IOException{
        return in.readChar();
    }
    public float readFloat() throws IOException{
        return in.readFloat();
    }
    public boolean readBoolean() throws IOException{
        return in.readBoolean();
    }
    public void close() throws IOException{
        in.close();
    }
}
