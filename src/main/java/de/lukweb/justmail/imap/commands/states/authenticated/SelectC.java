package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mail;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.storages.Mailboxes;
import de.lukweb.justmail.sql.storages.Mails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectC extends ImapCommand {

    public SelectC() {
        super("select");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {

        if (!session.checkForAuthentication(tag)) return;

        if (arguments.length < 1) {
            session.send(ImapResponse.BAD.create(tag, "Arguments invalid"));
            return;
        }

        Mailbox mailbox = Storages.get(Mailboxes.class).get(session.getUser(), arguments[0]);
        if (!session.checkForExistence(tag, mailbox)) return;

        Mails mails = Storages.get(Mails.class);
        List<Mail> userMail = mails.getAll(mailbox);

        List<Mail> unreadMails = userMail.stream().filter(mail -> !mail.isRead())
                .sorted((o1, o2) -> o1.getId() > o2.getId() ? 1 : -1).collect(Collectors.toList());
        Optional<Mail> firstUnread = unreadMails.stream().findFirst();

        session.send("* " + userMail.size() + " EXISTS");
        session.send("* " + unreadMails.stream().count() + " RECENT");

        firstUnread.ifPresent(mail -> session.send(ImapResponse.OK.create("*", "[UNSEEND " + mail.getId() +
                "] Message " + mail.getId() + " is first unseen")));

        session.send(ImapResponse.OK.create("*", "[UIDVALIDITY 3857529045] UIDs valid"));
        session.send(ImapResponse.OK.create("*", "[UIDNEXT " + mails.getMaxUID() + 1 + "] Predicted next UID"));
        session.send(ImapResponse.OK.create("*", "FLAGS (\\Answered \\Flagged \\Deleted \\Seen \\Draft)"));
        session.send(ImapResponse.OK.create("*", "[PERMANENTFLAGS (\\Deleted \\Seen \\*)] Limited"));

        session.send(ImapResponse.OK.create(tag, "[READ-WRITE] SELECT completed"));
        session.setSelected(mailbox);
    }
}
