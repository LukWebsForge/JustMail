package de.lukweb.justmail.imap.commands.states.not_authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;

public class LoginC extends ImapCommand {

    public LoginC() {
        super("login");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {

    }

}
