package de.lukweb.justmail.imap.commands.objects;

import de.lukweb.justmail.imap.ImapSession;

public abstract class ImapCommand {

    private String command;

    protected ImapCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public abstract void execute(String[] arguments, String tag, ImapSession session);
}
