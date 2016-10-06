package de.lukweb.justmail.imap.commands.states.any;

import de.lukweb.justmail.imap.ImapResponse;
import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;

public class CapabilityC extends ImapCommand {

    protected CapabilityC() {
        super("capability");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        session.send(ImapResponse.OK.create("*", "CAPABILITY IMAP4rev1 STARTTLS AUTH=PLAIN"));
        session.send(ImapResponse.OK.create(tag, "OK CAPABILITY completed"));
    }

}
