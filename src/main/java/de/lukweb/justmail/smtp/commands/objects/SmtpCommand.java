package de.lukweb.justmail.smtp.commands.objects;

import de.lukweb.justmail.smtp.SmtpCommands;
import de.lukweb.justmail.smtp.SmtpSession;

public abstract class SmtpCommand {

    private String command;

    protected SmtpCommand(String command) {
        this.command = command;
        SmtpCommands.registerCommand(this);
    }

    public String getCommand() {
        return command;
    }

    public abstract void execute(String[] arguments, SmtpSession session);
}
