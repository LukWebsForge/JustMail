package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

public class HelpC extends SmtpCommand {

    public HelpC() {
        super("HELP");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        Response.HELP_MESSAGE.create("Huston we have a problem!");
    }


}
