package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class VerifyC extends SmtpCommand {

    public VerifyC() {
        super("VRFY");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        if (!session.checkForForceSSL()) return;
        session.send(Response.COMMAND_NOT_IMPLEMENTED.create());
    }
}
