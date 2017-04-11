package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Users;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        if (arguments.length >= 1) {
            if (arguments[0].equalsIgnoreCase("PLAIN")) {
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
            } else if (arguments[0].equalsIgnoreCase("LOGIN")) {
                if (arguments.length == 1) {
                    //Username:
                    session.send(SmtpResponse.CONTINE_AUTH.customMessage("VXNlcm5hbWU6"));
                    session.setCallback(username -> {
                        //Password:
                        session.send(SmtpResponse.CONTINE_AUTH.customMessage("UGFzc3dvcmQ6"));
                        session.setCallback(password -> {
                            String auth = Base64.getEncoder().encodeToString(("\0" + new String(Base64.getDecoder().decode(username.trim()), StandardCharsets.UTF_8)
                                    + "\0" + new String(Base64.getDecoder().decode(password.trim()), StandardCharsets.UTF_8))
                                    .getBytes(StandardCharsets.UTF_8));
                            authenticateUser(session, auth);
                            return true;
                        });
                        return false;
                    });
                } else {
                    //Password:
                    session.send(SmtpResponse.CONTINE_AUTH.customMessage("UGFzc3dvcmQ6"));
                    session.setCallback(password -> {
                        String auth = Base64.getEncoder().encodeToString(("\0" + new String(Base64.getDecoder().decode(arguments[1].trim()), StandardCharsets.UTF_8)
                                + "\0" + new String(Base64.getDecoder().decode(password.trim()), StandardCharsets.UTF_8))
                                .getBytes(StandardCharsets.UTF_8));
                        authenticateUser(session, auth);
                        return true;
                    });
                }
                return;
            } else {
                session.send(SmtpResponse.ARGUMENT_NOT_IMPLEMENTED.create());
            }
        }
        session.send(SmtpResponse.ARGUMENT_ERROR.create());
    }

    private void authenticateUser(SmtpSession session, String plain) {
        User user = Storages.get(Users.class).getByBase64(plain.trim());
        if (user == null) {
            session.send(SmtpResponse.INVALID_CREDENTIALS.create());
            return;
        }
        session.setUser(user);
        session.send(SmtpResponse.AUTH_SUCCESSFUL.create());
    }

}
