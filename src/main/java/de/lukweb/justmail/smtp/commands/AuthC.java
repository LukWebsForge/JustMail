package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.crypto.CryptoUtils;
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
                        authenticateUser(session, cache.trim());
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
                            String plainPassword = new String(Base64.getDecoder().decode(password.trim()), StandardCharsets.UTF_8);
                            String email = ("\0" + new String(Base64.getDecoder().decode(username.trim()), StandardCharsets.UTF_8) + "\0");
                            authenticateUser(session, Base64.getEncoder().encodeToString((email + plainPassword).getBytes()));
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
        String base64Authenticate = new String(Base64.getDecoder().decode(plain), StandardCharsets.UTF_8);
        int lastIndex = base64Authenticate.lastIndexOf("\0");
        String[] data = {base64Authenticate.substring(0, lastIndex+1), base64Authenticate.substring(lastIndex+1)};
        char[] test = new char[data[0].length() + data[1].length()];
        System.arraycopy(data[0].toCharArray(), 0, test, 0, data[0].length());
        System.arraycopy(data[1].toCharArray(), 0, test, data[0].length(), data[1].length());
        User user = Storages.get(Users.class).getByBase64(Base64.getEncoder().encode(CryptoUtils.generateSHA512Password(test)));
        if (user == null) {
            session.send(SmtpResponse.INVALID_CREDENTIALS.create());
            return;
        }
        session.setUser(user);
        session.send(SmtpResponse.AUTH_SUCCESSFUL.create());
    }

}
