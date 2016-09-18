package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.storages.Domains;
import de.lukweb.justmail.utils.EmailAdress;

public class RcptC extends SmtpCommand {

    public RcptC() {
        super("RCPT");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        if (!session.checkForForceSSL()) return;
        if (arguments.length < 1) {
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        }
        String toFull = arguments[0];
        String[] toSplit = toFull.split(":");
        if (toSplit.length < 2) {
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        }
        String to = toSplit[1];
        EmailAdress toEmail = new EmailAdress(to);
        EmailAdress from = session.getFrom();
        Domains domains = Storages.get(Domains.class);
        if (from != null && !domains.isAllowed(toEmail.getDomain()) && !domains.isAllowed(from.getDomain())) {
            session.send(Response.BAD_SEQUENCE.create());
            return;
        }
        session.setTo(toEmail);
        session.send(Response.ACTION_OKAY.create());
    }

}
