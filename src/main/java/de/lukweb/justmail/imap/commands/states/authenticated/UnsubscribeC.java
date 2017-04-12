package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;

public class UnsubscribeC extends ImapCommand {

    public UnsubscribeC() {
        super("unsubscribe");
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
        session.removeSubscribed(mailbox);

        session.send(ImapResponse.OK.create(tag, "UNSUBSCRIBE completed"));
    }
}
