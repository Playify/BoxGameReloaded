package at.playify.boxgamereloaded.commands;

import at.playify.boxgamereloaded.BoxGameReloaded;

import java.lang.reflect.Field;

public class CommandBoolean extends Command {
    private final String cmd;
    private Field field;
    private String def;
    private Object o;

    CommandBoolean(String s, Field field, Object o) {
        cmd=s;
        this.field=field;
        this.o=o;
        this.def="toggle";
    }

    @Override
    public boolean is(String cmd) {
        return this.cmd.equals(cmd);
    }

    @Override
    public void run(String cmd, String[] args, BoxGameReloaded game) {
        try {
            String action=args.length==0 ? def : args[0];
            switch (action==null ? "" : action.toLowerCase()) {
                case "t":
                case "true":
                case "1":
                case "+":
                case "on":
                    set(true, game);
                    break;
                case "f":
                case "false":
                case "0":
                case "-":
                case "off":
                    set(false, game);
                    break;
                case "toggle":
                case "^":
                    set(!get(game), game);
                    break;
                case "get":
                    game.commandHandler.display(this.cmd+":"+get(game));
                    return;
                case "":
                    game.commandHandler.error("Syntax: "+this.cmd+" <true/false>");
                    return;
                default:
                    game.commandHandler.error("Illegal Argument!");
                    return;
            }
            game.commandHandler.display(this.cmd+" has been set to "+get(game));
        } catch (Exception e) {
            e.printStackTrace();
            game.commandHandler.error("Error executing Command.");
        }
    }

    protected boolean get(BoxGameReloaded game) throws IllegalAccessException {
        return field.getBoolean(o);
    }

    protected void set(boolean b, BoxGameReloaded game) throws IllegalAccessException {
        field.set(o, b);
        game.vars.loader.save();
    }
}
