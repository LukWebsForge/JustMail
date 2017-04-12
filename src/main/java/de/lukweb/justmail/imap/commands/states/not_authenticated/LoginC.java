package de.lukweb.justmail.imap.commands.states.not_authenticated;

import de.lukweb.justmail.crypto.CryptoUtils;
import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.responses.ImapPredefinedResponse;
import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Users;

import java.util.Arrays;

public class LoginC extends ImapCommand {

    public LoginC() {
        super("login");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {
        if (arguments.length < 2) {
            session.send(ImapPredefinedResponse.BAD_INVALID_ARGUMENTS.create(tag));
            return;
        }

        Users users = Storages.get(Users.class);

        String name = arguments[0];
        byte[] password = CryptoUtils.generateSHA512Password(arguments[1].toCharArray());

        User user = users.getByMail(name);
        if (user == null || !Arrays.equals(user.getPasswords().getPassword(), password)) {
            session.send(ImapResponse.BAD.create(tag, "user cannot be found or the password is incorrect"));
            return;
        }

        session.setUser(user);
    }

}
