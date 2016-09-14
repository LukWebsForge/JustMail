package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;
import de.lukweb.justmail.utils.StringUtils;

public class EhloC extends SmtpCommand {

    public EhloC() {
        super("EHLO");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (arguments.length < 1) {
            session.send(Response.ARGUMENT_ERROR.create());
            return;
        }
        String domain = StringUtils.removeNewLine(arguments[0]);
        session.setDomain(domain);
        String[] supportedExtensions = {
                "250-8BITMIME",
                "250-AUTH PLAIN",
                "250-CHUNKING",
                "250-DSN",
                "250-SIZE",
                "250-SMTPUTF8",
                "250 STARTTLS"
        };
        String ehloMessage = "250-" + JustMail.getInstance().getConfig().getHost() + " Hello " + domain + "\r\n" +
                String.join("\r\n", (CharSequence[]) supportedExtensions) + "\r\n";
        session.send(ehloMessage);
    }

}
