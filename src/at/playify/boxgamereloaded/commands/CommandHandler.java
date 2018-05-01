package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;
import at.playify.boxgamereloaded.interfaces.VariableContainer;
import at.playify.boxgamereloaded.util.Borrow;

import java.util.ArrayList;

public class CommandHandler {

    private final ArrayList<Command> commands;
    private final BoxGameReloaded game;

    public CommandHandler(BoxGameReloaded game) {
        this.game=game;
        commands=new ArrayList<>();
        Class<? extends VariableContainer> vars=game.vars.getClass();
        try {
            commands.add(new CommandBoolean("fly", vars.getField("fly"), game.vars));
            commands.add(new CommandBoolean("gravity", vars.getField("inverted_gravity"), game.vars));
            commands.add(new CommandBoolean("noclip", vars.getField("noclip"), game.vars));
            commands.add(new CommandBoolean("god", vars.getField("god"), game.vars));
            commands.add(new CommandConnect());
            commands.add(new CommandSkin());
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
        //TODO replace with HashMap<String,Command>
        for (int i=0;i<commands.size();i++) {
            Command command=commands.get(i);
            if (command.is(cmd)) {
                try {
                    command.run(cmd, args, game);
                } catch (Exception e) {
                    error(e.getClass().getSimpleName()+":"+e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
        }
        error("Unknown Command");
    }

    public void error(String s) {
        System.err.println(s);
    }

    public void display(String s) {
        System.out.println(s);
    }
}
