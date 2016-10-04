package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.mail.EmailAdress;
import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.storages.Users;

public class MailC extends SmtpCommand {

    public MailC() {
        super("MAIL");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        // Size param here
        if (!session.checkForHello()) return;
        if (!session.checkForForceSSL()) return;
        if (arguments.length < 1) {
            session.send(SmtpResponse.ARGUMENT_ERROR.create());
            return;
        }
        String fromComplete = arguments[0];
        String[] fromSplit = fromComplete.split(":");
        if (fromSplit.length < 2) {
            session.send(SmtpResponse.ARGUMENT_ERROR.create());
            return;
        }
        String from = fromComplete.split(":")[1];
        EmailAdress fromEmail = new EmailAdress(from);
        if (fromEmail.getAdress() == null || fromEmail.getAdress().isEmpty()) {
            session.send(SmtpResponse.ARGUMENT_ERROR.create());
            return;
        }
        EmailAdress to = session.getTo();
        Users users = Storages.get(Users.class);
        if (to != null && !users.exists(fromEmail.getAdress()) && !users.exists(to.getAdress())) {
            session.send(SmtpResponse.MAILBOX_NOT_FOUND.create());
            return;
        }
        if (users.exists(fromEmail.getAdress()) && (session.getUser() == null ||
                !fromEmail.getAdress().equalsIgnoreCase(session.getUser().getFullEmail()))) {
            session.send(SmtpResponse.AUTH_REQUIRED.create());
            return;
        }
        session.setFrom(fromEmail);
        if (session.getTo() != null) session.setTo(null);
        session.send(SmtpResponse.ACTION_OKAY.create());
    }

}
