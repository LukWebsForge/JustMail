package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;

public class VerifyC extends SmtpCommand {

    public VerifyC() {
        super("VRFY");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        if (!session.checkForForceSSL()) return;
        session.send(SmtpResponse.COMMAND_NOT_IMPLEMENTED.create());
    }
}
