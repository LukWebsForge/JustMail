package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;

/**
 * https://tools.ietf.org/html/rfc3501#section-6.3.6
 */
public class SubscribeC extends ImapCommand {

    public SubscribeC() {
        super("subscribe");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        if (!session.checkForAuthentication(tag)) return;

        if (arguments.length < 1) {
            session.send(ImapResponse.BAD.create(tag, "Invalid arguments"));
            return;
        }

        Mailbox mailbox = Storages.get(Mailboxes.class).get(session.getUser(), arguments[0]);
        if (!session.checkForExistence(tag, mailbox)) return;
        session.addSubscribed(mailbox);

        session.send(ImapResponse.OK.create(tag, "SUBSCRIBE completed"));
    }

}
