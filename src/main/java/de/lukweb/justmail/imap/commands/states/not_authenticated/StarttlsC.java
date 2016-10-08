package de.lukweb.justmail.imap.commands.states.not_authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;

public class StarttlsC extends ImapCommand {

    public StarttlsC() {
        super("starttls");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        session.send(ImapResponse.OK.create(tag, "Begin TLS negotiation now"));
        session.upgradeToSSL();
    }

}
