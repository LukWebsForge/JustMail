package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

import java.util.Base64;

public class AuthC extends SmtpCommand {

    public AuthC() {
        super("AUTH");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForForceSSL()) return;
        if (session.isAuthenticated()) {
            session.send(Response.BAD_SEQUENCE.customMessage("You're already logged in!"));
            return;
        }
        JustLogger.logger().fine(new String(Base64.getDecoder().decode(arguments[0])));

    }

}
