package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;
import de.lukweb.justmail.utils.StringUtils;

public class HeloC extends SmtpCommand {

    public HeloC() {
        super("HELO");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (arguments.length < 1) {
            session.send(SmtpResponse.ARGUMENT_ERROR.create());
            return;
        }
        String domain = StringUtils.removeNewLine(arguments[0]);
        session.setDomain(domain);
        session.send(SmtpResponse.SERVICE_READY.customMessage("Hello " + domain + ", How are you?"));
    }
}
