package de.lukweb.justmail.smtp;

import de.lukweb.justmail.smtp.command.*;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

import java.util.Arrays;
import java.util.HashMap;

public class SmtpCommands {

    private static HashMap<String, SmtpCommand> commands = new HashMap<>();

    static {
        new AuthC();
        new DataC();
        new EhloC();
        new HeloC();
        new HeloC();
        new HelpC();
        new MailC();
        new NoopC();
        new QuitC();
        new RcptC();
        new RsetC();
        new StartTlsC();
        new VerifyC();
    }

    public static void registerCommand(SmtpCommand command) {
        commands.put(command.getCommand().toUpperCase(), command);
    }

    private static SmtpCommand getCommand(String command) {
        return commands.get(command.toUpperCase());
    }

    public static void handleCommand(String commandStr, SmtpSession session) {
        String[] commandSplit = commandStr.split(" ");
        String commandName = commandSplit[0].trim();

        SmtpCommand command = getCommand(commandName);
        if (command == null) {
            session.send(Response.COMMAND_UNRECOGNIZED.create());
            return;
        }

        command.execute(Arrays.copyOfRange(commandSplit, 1, commandSplit.length), session);

    }

}
