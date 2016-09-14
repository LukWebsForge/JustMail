package de.lukweb.justmail.console.commands.objects;

import de.lukweb.justmail.console.commands.DomainC;
import de.lukweb.justmail.console.commands.HelpC;
import de.lukweb.justmail.console.commands.StopC;
import de.lukweb.justmail.console.commands.UserC;

import java.util.ArrayList;

public class ConsoleCommands {

    private static ArrayList<ConsoleCommand> commands = new ArrayList<>();

    static {
        reg(new DomainC());
        reg(new HelpC());
        reg(new UserC());
        reg(new StopC());
    }

    private static void reg(ConsoleCommand cmd) {
        commands.add(cmd);
    }

    public static ArrayList<ConsoleCommand> getAll() {
        return commands;
    }

    public static ConsoleCommand get(String command) {
        for (ConsoleCommand cmd : commands) if (cmd.getCommand().equalsIgnoreCase(command.trim())) return cmd;
        return null;
    }
}
