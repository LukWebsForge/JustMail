package de.lukweb.justmail.imap.commands.states.any;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;

public class CapabilityC extends ImapCommand {

    public CapabilityC() {
        super("capability");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        session.send(ImapResponse.OK.create("*", "CAPABILITY IMAP4rev1 STARTTLS AUTH=PLAIN"));
        session.send(ImapResponse.OK.create(tag, "OK CAPABILITY completed"));
    }

}
