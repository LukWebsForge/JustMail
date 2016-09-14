package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class RsetC extends SmtpCommand {

    public RsetC() {
        super("RSET");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        session.resetMailData();
        session.send(Response.ACTION_OKAY.create());
    }
}
