package de.lukweb.justmail.imap.commands.states.not_authenticated;

import de.lukweb.justmail.console.JustLogger;
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
                session.send("+ \r\n");
                session.setCallback(cache -> {
                    JustLogger.logger().info(cache);
                    authenticateUser(session, cache, tag);
                    return true;
                });
            } else {
                authenticateUser(session, arguments[1], tag);
            }
            return;
        } else if (arguments[0].equalsIgnoreCase("LOGIN")) {
            if (arguments.length == 1) {
                //Username:
                session.send("+ VXNlcm5hbWU6\r\n");
                session.setCallback(username -> {
                    //Password:
                    session.send("+ UGFzc3dvcmQ6\r\n");
                    session.setCallback(password -> {
                        String auth = Base64.getEncoder().encodeToString(("\0" + new String(Base64.getDecoder().decode(username.trim()), StandardCharsets.UTF_8)
                                + "\0" + new String(Base64.getDecoder().decode(password.trim()), StandardCharsets.UTF_8))
                                .getBytes(StandardCharsets.UTF_8));
                        authenticateUser(session, auth, tag);
                        return true;
                    });
                    return false;
                });
            } else {
                //Password:
                session.send("+ UGFzc3dvcmQ6\r\n");
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
        User user = Storages.get(Users.class).getByBase64(plain.trim());
        if (user == null) {
            session.send(ImapResponse.NO.create(tag, "Authentication credentials invalid"));
            return;
        }
        session.setUser(user);
        session.send(ImapResponse.OK.create(tag, "Authenticated"));
    }
}
