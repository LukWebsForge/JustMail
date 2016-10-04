package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;

public class NoopC extends SmtpCommand {

    public NoopC() {
        super("NOOP");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.send(SmtpResponse.ACTION_OKAY.create());
    }

}
