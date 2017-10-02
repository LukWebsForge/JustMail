package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;

/**
 * https://tools.ietf.org/html/rfc3501#section-6.3.4
 */
public class DeleteC extends ImapCommand {

    public DeleteC() {
        super("delete");
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

        if (session.getSelected() != null && session.getSelected().equals(mailbox)) session.setSelected(null);
        Storages.get(Mailboxes.class).delete(mailbox);

        session.send(ImapResponse.OK.create(tag, "DELETE Completed"));
    }
}
