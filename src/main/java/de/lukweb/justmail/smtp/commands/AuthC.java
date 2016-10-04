package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Users;

public class AuthC extends SmtpCommand {

    public AuthC() {
        super("AUTH");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForForceSSL()) return;
        if (session.isAuthenticated()) {
            session.send(SmtpResponse.BAD_SEQUENCE.customMessage("You're already logged in!"));
            return;
        }
        if (arguments.length >= 1 && arguments[0].equalsIgnoreCase("PLAIN")) {
            if (arguments.length == 1) {
                session.send(SmtpResponse.CONTINE_AUTH.create());
                session.setCallback(cache -> {
                    if (cache.equals("=")) return true;
                    authenticateUser(session, cache);
                    return true;
                });
            } else {
                authenticateUser(session, arguments[1]);
            }
            return;
        }
        session.send(SmtpResponse.ARGUMENT_ERROR.create());
    }

    private void authenticateUser(SmtpSession session, String plain) {
        User user = Storages.get(Users.class).getByBase64(plain);
        if (user == null) {
            session.send(SmtpResponse.INVALID_CREDENTIALS.create());
            return;
        }
        session.setUser(user);
        session.send(SmtpResponse.AUTH_SUCCESSFUL.create());
    }

}
