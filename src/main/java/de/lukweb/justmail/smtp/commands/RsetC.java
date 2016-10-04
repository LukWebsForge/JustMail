package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;

public class RsetC extends SmtpCommand {

    public RsetC() {
        super("RSET");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        session.resetMailData();
        session.send(SmtpResponse.ACTION_OKAY.create());
    }
}
