package de.lukweb.justmail.imap;

import de.lukweb.justmail.imap.commands.objects.ImapCommand;
import de.lukweb.justmail.imap.commands.states.any.CapabilityC;
import de.lukweb.justmail.imap.commands.states.any.LogoutC;
import de.lukweb.justmail.imap.commands.states.any.NoopC;
import de.lukweb.justmail.imap.commands.states.authenticated.*;
import de.lukweb.justmail.imap.commands.states.not_authenticated.AuthenticateC;
import de.lukweb.justmail.imap.commands.states.not_authenticated.LoginC;
import de.lukweb.justmail.imap.commands.states.not_authenticated.StarttlsC;
import de.lukweb.justmail.imap.commands.states.selected.*;
import de.lukweb.justmail.imap.responses.ImapPredefinedResponse;

import java.util.Arrays;
import java.util.HashMap;

public class ImapCommands {

    private static HashMap<String, ImapCommand> commands = new HashMap<>();

    static {
        new CapabilityC();
        new LogoutC();
        new NoopC();

        new AppendC();
        new CreateC();
        new DeleteC();
        new ExamineC();
        new ListC();
        new LsubC();
        new RenameC();
        new SelectC();
        new StatusC();
        new SubscribeC();
        new UnsubscribeC();

        new AuthenticateC();
        new LoginC();
        new StarttlsC();

        new CheckC();
        new CloseC();
        new CopyC();
        new ExpungeC();
        new FetchC();
        new SearchC();
        new StoreC();
        new UidC();
    }

    public static void registerCommand(ImapCommand command) {
        commands.put(command.getCommand().toUpperCase(), command);
    }

    public static void handleCommand(String commandStr, String tag, ImapSession session) {
        String[] commandSplit = commandStr.replaceAll("\\n", "").replaceAll("\\r", "").split(" ");
        String commandName = commandSplit[0].trim();

        ImapCommand command = getCommand(commandName);
        if (command == null) {
            session.send(ImapPredefinedResponse.BAD_COMMNAD_UNKNOW.create(tag));
            return;
        }

        command.execute(Arrays.copyOfRange(commandSplit, 1, commandSplit.length), tag, session);
    }

    private static ImapCommand getCommand(String command) {
        return commands.get(command.toUpperCase());
    }


}
