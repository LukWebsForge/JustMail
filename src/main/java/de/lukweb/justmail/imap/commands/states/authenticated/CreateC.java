package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;

/**
 * https://tools.ietf.org/html/rfc3501#section-6.3.3
 */
public class CreateC extends ImapCommand {

    public CreateC() {
        super("create");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        if (!session.checkForAuthentication(tag)) return;
        if (arguments.length < 1) {
            session.send(ImapResponse.BAD.create(tag, "Arguments invalid"));
            return;
        }
        // todo add split thing
        Mailbox mailbox = Storages.get(Mailboxes.class).get(session.getUser(), arguments[0]);
        if (mailbox != null) {
            session.send(ImapResponse.NO.create(tag, "Mailbox already exists"));
            return;
        }
        mailbox = new Mailbox(-1, arguments[0], session.getUser());
        Storages.get(Mailboxes.class).save(mailbox);
        session.send(ImapResponse.OK.create(tag, "CREATE completed"));
    }
}
