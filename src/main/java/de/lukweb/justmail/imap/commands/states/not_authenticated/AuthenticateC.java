package de.lukweb.justmail.imap.commands.states.not_authenticated;

import de.lukweb.justmail.crypto.CryptoUtils;
import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapPredefinedResponse;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Users;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthenticateC extends ImapCommand {

    public AuthenticateC() {
        super("authenticate");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        if (arguments.length < 1) {
            session.send(ImapPredefinedResponse.BAD_INVALID_ARGUMENTS.create(tag));
            return;
        }

        if (arguments[0].equalsIgnoreCase("PLAIN")) {
            if (arguments.length == 1) {
                session.send("+ ");
                session.setCallback(cache -> {
                    authenticateUser(session, cache.trim(), tag);
                    return true;
                });
            } else {
                authenticateUser(session, arguments[1], tag);
            }
            return;
        } else if (arguments[0].equalsIgnoreCase("LOGIN")) {
            if (arguments.length == 1) {
                //Username:
                session.send("+ VXNlcm5hbWU6");
                session.setCallback(username -> {
                    //Password:
                    session.send("+ UGFzc3dvcmQ6");
                    session.setCallback(password -> {
                        String plainPassword = new String(Base64.getDecoder().decode(password.trim()), StandardCharsets.UTF_8);
                        String email = ("\0" + new String(Base64.getDecoder().decode(username.trim()), StandardCharsets.UTF_8) + "\0");
                        authenticateUser(session, Base64.getEncoder().encodeToString((email + plainPassword).getBytes()), tag);
                        return true;
                    });
                    return false;
                });
            } else {
                //Password:
                session.send("+ UGFzc3dvcmQ6");
                session.setCallback(password -> {
                    String auth = Base64.getEncoder().encodeToString(("\0" + new String(Base64.getDecoder().decode(arguments[1].trim()), StandardCharsets.UTF_8)
                            + "\0" + new String(Base64.getDecoder().decode(password.trim()), StandardCharsets.UTF_8))
                            .getBytes(StandardCharsets.UTF_8));
                    authenticateUser(session, auth, tag);
                    return true;
                });
            }
        } else {
            session.send(ImapResponse.BAD.create(tag, "Unrecognized authentication type"));
        }
    }

    private void authenticateUser(ImapSession session, String plain, String tag) {
        String base64Authenticate = new String(Base64.getDecoder().decode(plain), StandardCharsets.UTF_8);
        int lastIndex = base64Authenticate.lastIndexOf("\0");
        String[] data = {base64Authenticate.substring(0, lastIndex+1), base64Authenticate.substring(lastIndex+1)};
        char[] test = new char[data[0].length() + data[1].length()];
        System.arraycopy(data[0].toCharArray(), 0, test, 0, data[0].length());
        System.arraycopy(data[1].toCharArray(), 0, test, data[0].length(), data[1].length());
        User user = Storages.get(Users.class).getByBase64(Base64.getEncoder().encode(CryptoUtils.generateSHA512Password(test)));
        if (user == null) {
            session.send(ImapResponse.NO.create(tag, "Authentication credentials invalid"));
            return;
        }
        session.setUser(user);
        session.send(ImapResponse.OK.create(tag, "Authenticated"));
    }
}
