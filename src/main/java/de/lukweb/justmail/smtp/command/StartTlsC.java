package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class StartTlsC extends SmtpCommand {

    public StartTlsC() {
        super("STARTTLS");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.send(Response.SERVICE_READY.customMessage("Go ahead"));
        session.resetAll();
        session.upgradeToSSL();
    }

}
