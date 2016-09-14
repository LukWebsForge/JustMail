package de.lukweb.justmail.console.commands;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.console.commands.objects.ConsoleCommand;

public class StopC implements ConsoleCommand {

    @Override
    public String getCommand() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops this mail server";
    }

    @Override
    public String getSyntax() {
        return "stop";
    }

    @Override
    public void execute(String[] args) {
        JustLogger.logger().warning("Stopping now...");
        JustMail.getInstance().stop();
    }

}
