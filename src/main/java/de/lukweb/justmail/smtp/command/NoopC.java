package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class NoopC extends SmtpCommand {

    public NoopC() {
        super("NOOP");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.send(Response.ACTION_OKAY.create());
    }

}
