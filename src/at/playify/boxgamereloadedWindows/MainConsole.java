package at.playify.boxgamereloadedWindows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class MainConsole {
    public static void main(String[] args) {
        try {
            final Socket socket=new Socket("127.0.0.1", Integer.parseInt(args[0]));
            final PrintStream out=new PrintStream(socket.getOutputStream());
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    out.println("\b\b");
                    out.flush();
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s;
            while ((s=br.readLine())!=null) {
                try {
                    out.println(s);
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
