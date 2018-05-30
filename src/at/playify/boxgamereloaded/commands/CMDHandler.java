package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.VariableContainer;
import at.playify.boxgamereloaded.util.Borrow;

import java.util.ArrayList;
import java.util.HashMap;

public class CMDHandler {

    private final HashMap<String,Command> commands;
    private final BoxGameReloaded game;

    public CMDHandler(BoxGameReloaded game) {
        this.game=game;
        commands=new HashMap<>();
        Class<? extends VariableContainer> vars=game.vars.getClass();
        try {
            commands.put("fly",new CommandBoolean("fly", vars.getField("fly"), game.vars));
            commands.put("gravity",new CommandBoolean("gravity", vars.getField("inverted_gravity"), game.vars));
            commands.put("noclip",new CommandBoolean("noclip", vars.getField("noclip"), game.vars));
            commands.put("god",new CommandBoolean("god", vars.getField("god"), game.vars));
            commands.put("connect",new CommandConnect());
            commands.put("skin", new CommandSkin());
            commands.put("tail", commands.get("skin"));
            commands.put("2d", new Command23Dimension());
            commands.put("3d", commands.get("2d"));
            commands.put("size", new CommandSize());
            commands.put("shift", new CommandShift());
            commands.put("name", new CommandName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String s) {
        ArrayList<String> strings=Borrow.stringList();
        char[] chars=s.toCharArray();
        StringBuilder str=new StringBuilder();
        boolean escape=false, quote=false;
        for (char c : chars) {
            if (!escape&&c=='\\') escape=true;
            else if (!escape&&c=='"') quote^=true;
            else if (!escape&&!quote&&c==' ') {
                if (str.length()!=0) {
                    strings.add(str.toString());
                    str.setLength(0);
                }
                escape=false;
            } else {
                if (escape&&c=='n') c='\n';
                else if (escape&&c=='b') c='\b';
                else if (escape&&c=='r') c='\r';
                else if (escape&&c=='t') c='\t';
                str.append(c);
                escape=false;
            }
        }
        if (quote) {
            error("Illegal Command Arguments: open \" on end of Command");
            return;
        }
        if (escape) {
            error("Illegal Command Arguments: \\ on end of Command");
            return;
        }

        if (str.length()!=0) {
            strings.add(str.toString());
        }
        if (strings.isEmpty()) {
            error("Empty Command");
            return;
        }
        String cmd=strings.remove(0);
        String[] args=strings.toArray(new String[0]);
        execute(cmd, args);
    }

    private void execute(String cmd, String... args) {
        if (commands.containsKey(cmd)){
            try {
                commands.get(cmd).run(cmd, args, game);
            } catch (Exception e) {
                error(e.getClass().getSimpleName()+":"+e.getMessage());
                e.printStackTrace();
            }
            return;
        }
        error("Unknown Command");
    }

    public void error(String s) {
        game.logger.error(s);
    }

    public void display(String s) {
        game.logger.show(s);
    }
}
