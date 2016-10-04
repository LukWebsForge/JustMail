package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;

public class StartTlsC extends SmtpCommand {

    public StartTlsC() {
        super("STARTTLS");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.send(SmtpResponse.SERVICE_READY.customMessage("Go ahead"));
        session.resetAll();
        session.upgradeToSSL();
    }

}
