package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;
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
            session.send(Response.BAD_SEQUENCE.customMessage("You're already logged in!"));
            return;
        }
        if (arguments.length == 0) {
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        } else if (arguments.length == 1) {
            if (!arguments[0].equalsIgnoreCase("PLAIN")) {
                session.send(Response.ARGUMENT_ERROR.create());
                return;
            }
            // todo plain save stuff
        } else if (arguments.length == 2) {
            if (!arguments[0].equalsIgnoreCase("PLAIN")) {
                session.send(Response.ARGUMENT_ERROR.create());
                return;
            }
            User user = Storages.get(Users.class).getByBase64(arguments[1]);
            if (user == null) {
                session.send(Response.INVALID_CREDENTIALS.create());
                return;
            }
            session.setUser(user);
        }
    }

}
