package de.lukweb.justmail.imap.commands.states.any;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;

public class LogoutC extends ImapCommand {

    public LogoutC() {
        super("logout");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        session.sayGoodbye();
        session.send(ImapResponse.OK.create(tag, "LOGOUT completed"));
        session.close();
    }

}
