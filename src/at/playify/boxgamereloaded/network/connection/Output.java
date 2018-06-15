package at.playify.boxgamereloaded.network.connection;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Output implements Closeable {
    private DataOutputStream out;

    public Output(OutputStream s){
        out=new DataOutputStream(s);
    }

    public void writeString(String s) throws IOException{
        out.writeInt(s.length());
        out.writeChars(s);
    }

    public void writeInt(int i) throws IOException{
        out.writeInt(i);
    }
    public void writeChar(char i) throws IOException{
        out.writeChar(i);
    }
    public void writeFloat(float i) throws IOException{
        out.writeFloat(i);
    }
    public void writeBoolean(boolean i) throws IOException{
        out.writeBoolean(i);
    }
    public void flush() throws IOException{
        out.flush();
    }
    public void close() throws IOException{
        out.close();
    }
}
