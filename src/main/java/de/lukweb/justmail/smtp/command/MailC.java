package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.storages.Domains;
import de.lukweb.justmail.utils.EmailAdress;

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
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        }
        String fromComplete = arguments[0];
        String[] fromSplit = fromComplete.split(":");
        if (fromSplit.length < 2) {
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        }
        String from = fromComplete.split(":")[1];
        EmailAdress fromEmail = new EmailAdress(from);
        if (fromEmail.getAdress() == null || fromEmail.getAdress().isEmpty()) {
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        }
        EmailAdress to = session.getTo();
        Domains domains = Storages.get(Domains.class);
        if (to != null && !domains.isAllowed(session.getFrom().getDomain()) && !domains.isAllowed(to.getDomain())) {
            session.send(Response.BAD_SEQUENCE.create());
            return;
        }
        session.setFrom(fromEmail);
        session.send(Response.ACTION_OKAY.create());
    }

}
