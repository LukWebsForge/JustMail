package de.lukweb.justmail.console.commands;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.console.commands.objects.ConsoleCommand;
import de.lukweb.justmail.console.commands.objects.ConsoleCommands;

import java.util.ArrayList;

public class HelpC implements ConsoleCommand {

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows you a list with all commands";
    }

    @Override
    public String getSyntax() {
        return "help [command]";
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 0) {
            ConsoleCommand cmd = ConsoleCommands.get(args[0]);
            if (cmd == null) {
                JustLogger.logger().warning("The command [" + args[0].toLowerCase() + "] cannot be found!");
                return;
            }
            String helpStr = "\n <<< HELP FOR [" + cmd.getCommand().toLowerCase() + "] >>> \n";
            helpStr += "Command: " + cmd.getCommand() + "\n";
            helpStr += "Syntax: " + cmd.getSyntax() + "\n";
            helpStr += "Description: " + cmd.getDescription() + "\n";

            JustLogger.logger().info(helpStr);
            return;
        }
        ArrayList<ConsoleCommand> cmds = ConsoleCommands.getAll();
        final String[] helpStr = {"\n <<< HELP >>> \n"};

        cmds.stream()
                .sorted((cmd1, cmd2) -> cmd1.getCommand().compareToIgnoreCase(cmd2.getCommand()))
                .forEach(cmd -> helpStr[0] += "[" + cmd.getCommand().toLowerCase() + "] - " + cmd.getDescription() +
                        "\n");

        helpStr[0] += "\nIf you want to see more information about a command use [help [command]].\n";
        helpStr[0] += "\nJustMail -> Thanks to all contributers -> https://github.com/lukweb-de/just-mail\n";

        JustLogger.logger().info(helpStr[0]);
    }
}
