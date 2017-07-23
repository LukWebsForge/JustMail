package de.lukweb.justmail.imap.commands.states.authenticated;

import de.lukweb.justmail.imap.ImapSession;
import de.lukweb.justmail.imap.commands.objects.ImapCommand;

/**
 * https://tools.ietf.org/html/rfc3501#section-6.3.9
 */
public class LsubC extends ImapCommand {

    public LsubC() {
        super("lsub");
    }

    @Override
    public void execute(String[] arguments, String tag, ImapSession session) {

    }

}
