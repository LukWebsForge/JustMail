package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;

public class HelpC extends SmtpCommand {

    public HelpC() {
        super("HELP");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.send(SmtpResponse.HELP_MESSAGE.create("Huston we have a problem!"));
    }


}
