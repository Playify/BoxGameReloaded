package at.playify.boxgamereloaded.windows;

import at.playify.boxgamereloaded.network.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainServer {
    public static void main(String[] args){
        Server server=new Server(new WindowsHandler());
        server.start();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s;
            while ((s = br.readLine()) != null) {
                try {
                    server.broadcastRaw(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
