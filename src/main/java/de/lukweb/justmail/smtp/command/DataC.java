package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class DataC extends SmtpCommand {

    public DataC() {
        super("DATA");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        if (!session.checkForForceSSL()) return;
        session.send(Response.START_DATA.create());
        session.setReadData(true);
    }

}
