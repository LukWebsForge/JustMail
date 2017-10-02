package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;

public class RenameC extends ImapCommand {

    public RenameC() {
        super("rename");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        if (!session.checkForAuthentication(tag)) return;
        if (arguments.length < 2) {
            session.send(ImapResponse.BAD.create(tag, "Arguments invalid"));
            return;
        }
        // todo add split thing
        Mailbox mailbox = Storages.get(Mailboxes.class).get(session.getUser(), arguments[0]);
        if (mailbox == null) {
            session.send(ImapResponse.NO.create(tag, "Mailbox does not exist"));
            return;
        }

        mailbox.setName(arguments[1]);
        Storages.get(Mailboxes.class).save(mailbox);
        session.send(ImapResponse.OK.create(tag, "RENAME completed"));

    }
}
