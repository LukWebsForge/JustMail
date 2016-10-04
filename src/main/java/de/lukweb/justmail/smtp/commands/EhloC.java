package de.lukweb.justmail.smtp.commands;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.config.Config;
import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.commands.objects.SmtpCommand;
import de.lukweb.justmail.utils.StringUtils;

import java.util.ArrayList;

public class EhloC extends SmtpCommand {

    public EhloC() {
        super("EHLO");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (arguments.length < 1) {
            session.send(SmtpResponse.ARGUMENT_ERROR.create());
            return;
        }
        String domain = StringUtils.removeNewLine(arguments[0]);
        session.setDomain(domain);
        Config config = JustMail.getInstance().getConfig();
        /*
        Extensions I want to implement

        250-8BITMIME
        250-CHUNKING
        250-DSN
        250-SMTPUTF8
        */
        ArrayList<String> supportedExtensions = new ArrayList<>();

        supportedExtensions.add(config.getHost() + " Hello " + domain);
        supportedExtensions.add("8BITMIME");
        supportedExtensions.add("AUTH PLAIN");
        supportedExtensions.add("SIZE " + config.getMaxMailSize());
        if (!session.isUsingSSL()) supportedExtensions.add("STARTTLS");

        String extensionsStr = "";

        for (int i = 0; i < supportedExtensions.size(); i++) {
            if (i + 1 >= supportedExtensions.size()) {
                extensionsStr += "250 " + supportedExtensions.get(i);
            } else {
                extensionsStr += "250-" + supportedExtensions.get(i);
            }
            extensionsStr += "\r\n";
        }

        session.send(extensionsStr);
    }

}
