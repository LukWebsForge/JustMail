package de.lukweb.justmail.imap.commands.states.any;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;

public class NoopC extends ImapCommand {

    public NoopC() {
        super("noop");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        session.send(ImapResponse.OK.create(tag, "NOOP completed"));
    }

}
