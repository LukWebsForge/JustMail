package de.lukweb.justmail.smtp.command;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;
import de.lukweb.justmail.smtp.command.objects.SmtpCommand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DataC extends SmtpCommand {

    public DataC() {
        super("DATA");
    }

    @Override
    public void execute(String[] arguments, SmtpSession session) {
        if (!session.checkForHello()) return;
        if (!session.checkForForceSSL()) return;
        if (session.getTo() == null || session.getFrom() == null) {
            session.send(Response.BAD_SEQUENCE.create());
            return;
        }
        session.send(Response.START_DATA.create());
        session.setReadData(true);

        byte[] buffer = new byte[1024 * 64];
        ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();

        // todo bug

        session.setCallback(cache -> {
            if (cache.trim().equalsIgnoreCase(".")) {
                if (arrayOut.size() > JustMail.getInstance().getConfig().getMaxMailSize()) {
                    session.send(Response.NO_STORAGE_LEFT.create());
                } else {
                    session.setData(arrayOut.toByteArray());
                    session.save();
                    session.send(Response.ACTION_OKAY.create());
                }
                session.resetMailData();
                session.send(Response.ACTION_OKAY.create());
                return true;
            }
            System.out.println(arrayOut.size());
            if (arrayOut.size() > JustMail.getInstance().getConfig().getMaxMailSize()) return false;
            try {
                arrayOut.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

}
