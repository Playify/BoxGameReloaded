package at.playify.boxgamereloaded.network.packet;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.level.ServerLevel;
import at.playify.boxgamereloaded.network.Server;
import at.playify.boxgamereloaded.network.connection.ConnectionToClient;
import at.playify.boxgamereloaded.network.connection.ConnectionToServer;
import at.playify.boxgamereloaded.network.connection.Input;
import at.playify.boxgamereloaded.network.connection.Output;
import at.playify.boxgamereloaded.util.Action;
import at.playify.boxgamereloaded.util.json.JSONException;
import at.playify.boxgamereloaded.util.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class PacketPaintMenuAction extends Packet {
    private int variant;
    private int buttonVariant;
    private boolean zoom;

    public PacketPaintMenuAction(int variant, int buttonVariant) {
        this.variant=variant;
        this.buttonVariant=buttonVariant;
    }

    public PacketPaintMenuAction() {
    }

    @Override
    public void handle(BoxGameReloaded game, ConnectionToServer connectionToServer) {/*
        int x=variant;
        int y=buttonVariant;
        game.player.genBound.move(x,y);
        game.zoom_x+=x;
        game.zoom_y+=y;
        game.zoom_x=Utils.clamp(game.zoom_x, 0, game.level.sizeX);
        game.zoom_y=Utils.clamp(game.zoom_y, 0, game.level.sizeY);
        game.level.spawnPoint.shift(x,y,game.level.sizeX,game.level.sizeY);
        game.vars.check.shift(x,y,game.level.sizeX,game.level.sizeY);*/
    }

    @Override
    public void handle(final Server server, final ConnectionToClient connectionToClient) {
            server.levels.getLevel(connectionToClient.world, new Action<ServerLevel>() {
                @Override
                public void exec(ServerLevel level) {
                    int x=-((buttonVariant&1)!=0?(buttonVariant&2)-1:0);
                    int y=-((buttonVariant&1)!=0?0:(buttonVariant&2)-1);
                    if ((buttonVariant&4)!=0){
                        if (x==0) x=-y;
                        else y=x;
                    }
                    if (variant==1) {
                        if(level.sizeX<=0)x=0;
                        if(level.sizeY<=0)y=0;
                        shift(level, x, y);
                    }else if (variant==0){
                        size(level,x!=0?1:0,y!=0?1:0);
                        int xx=x>0 ? 1 : 0;
                        int yy=y>0 ? 1 : 0;
                        shift(level, -xx, -yy);
                    }else if (variant==2){
                        int xx=x>0 ? 1 : 0;
                        int yy=y>0 ? 1 : 0;
                        shift(level, xx, yy);
                        size(level,x!=0?-1:0,y!=0?-1:0);
                        if(level.sizeX<=0)xx=0;
                        if(level.sizeY<=0)yy=0;
                    }
                    server.broadcast(new PacketLevelData(level.toWorldString()),connectionToClient.world,connectionToClient);
                    if (connectionToClient.world.startsWith("paint")) {
                        try {
                            JSONObject paint=server.handler.read("paint");
                            if (!paint.has(connectionToClient.world)) {
                                paint.put(connectionToClient.world, new JSONObject());
                            }
                            JSONObject lvl=paint.getJSONObject(connectionToClient.world);
                            lvl.put("data", level.toWorldString());
                            if (connectionToClient.skin!=null) {
                                lvl.put("by", connectionToClient.skin.substring(connectionToClient.skin.lastIndexOf(';')+1));
                                final PacketMainMenu packet=new PacketMainMenu();

                                server.levels.getLevels("paint", new Action<ArrayList<String>>() {
                                    @Override
                                    public void exec(ArrayList<String> strings) {
                                        packet.name="paint";
                                        packet.list=strings;
                                        server.broadcast(packet);
                                    }
                                });
                            }
                            server.handler.write("paint", paint);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    @Override
    public void send(Output out, Server server, ConnectionToClient con) throws IOException{
        out.writeInt(variant);
        out.writeInt(buttonVariant);
        out.writeBoolean(zoom);
    }

    @Override
    public void send(Output out, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        out.writeInt(variant);
        out.writeInt(buttonVariant);
        out.writeBoolean(zoom);
    }

    @Override
    public void receive(Input in, Server server, ConnectionToClient con) throws IOException{
        variant=in.readInt();
        buttonVariant=in.readInt();
        zoom=in.readBoolean();
    }

    @Override
    public void receive(Input in, BoxGameReloaded game, ConnectionToServer con) throws IOException{
        variant=in.readInt();
        buttonVariant=in.readInt();
        zoom=in.readBoolean();
    }

    private void shift(ServerLevel level, int x, int y) {
        x=level.sizeX==0?0:((x%level.sizeX)+level.sizeX)%level.sizeX;
        y=level.sizeY==0?0:((y%level.sizeY)+level.sizeY)%level.sizeY;
        if (x==0&&y==0) {
            return;
        }
        char[] blocks=level.blocks;
        int[] metas=level.metas;
        char[] blk=new char[blocks.length];
        int[] mta=new int[metas.length];
        for (int i=0;i<blocks.length;i++) {
            int ii=(i+x)%(blocks.length);
            if (x!=0&&ii%level.sizeX<x) {
                ii+=blocks.length-level.sizeX;
            }
            ii+=y*level.sizeX;
            blk[i]=blocks[ii%blocks.length];
            mta[i]=metas[ii%blocks.length];
        }
        level.blocks=blk;
        level.metas=mta;
        level.spawnPoint.shift(-x,-y,level.sizeX,level.sizeY);
    }

    private void size(ServerLevel level, int x, int y) {
        if (x==0&&y==0) {
            return;
        }
        x+=level.sizeX;
        y+=level.sizeY;
        if (x<0)x=0;
        if (y<0)y=0;
        char[] blocks=level.blocks;
        int[] metas=level.metas;
        char[] blk=new char[x*y];
        int[] mta=new int[x*y];
        int yy=Math.min(y, level.sizeY);
        int xx=Math.min(x, level.sizeX);
        for (int i=0;i<yy;i++) {
            System.arraycopy(blocks,level.sizeX*i,blk,x*i,xx);
            System.arraycopy(metas,level.sizeX*i,mta,x*i,xx);
        }
        level.sizeX=x;
        level.sizeY=y;
        level.blocks=blk;
        level.metas=mta;
    }
}
