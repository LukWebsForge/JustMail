package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

import java.io.IOException;

public class QuitC extends SmtpCommand {

    public QuitC() {
        super("QUIT");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        session.send(Response.SERVICE_CLOSING.create());
        try {
            session.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
