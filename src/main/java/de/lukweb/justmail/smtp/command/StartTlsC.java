package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class StartTlsC extends SmtpCommand {

    public StartTlsC() {
        super("STARTTLS");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.resetAll();
        session.upgradeToSSL();
    }

}
