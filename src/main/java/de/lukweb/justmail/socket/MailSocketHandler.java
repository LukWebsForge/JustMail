package de.lukweb.justmail.socket;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.smtp.SmtpCommands;
import de.lukweb.justmail.smtp.Response;
import de.lukweb.justmail.smtp.SmtpSession;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MailSocketHandler implements Runnable {

    private Socket socket;

    public MailSocketHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            JustLogger.logger().fine("New connection from " + socket.getInetAddress().getHostAddress());

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String cache = "";
            SmtpSession session = new SmtpSession(socket, in, out);

            String ownDomain = JustMail.getInstance().getConfig().getHost();

            // First we send greetings
            out.writeUTF(Response.SERVICE_READY.create(ownDomain));
            out.flush();

            boolean telnetCommand = false;
            byte[] buffer = new byte[1024 * 64];
            ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();

            while (true) {
                int read = in.read();
                if (read == -1) break;
                if (read == 4) {
                    break;
                }
                if (read == 255) {
                    telnetCommand = true;
                    continue;
                }
                if (telnetCommand) {
                    telnetCommand = false;
                    continue;
                }
                cache += (char) read;
                if (!(cache.endsWith("\n\r") || cache.endsWith("\n"))) continue;
                if (session.isReadingData()) {
                    if (cache.trim().startsWith(".")) {
                        session.setReadData(false);
                        cache = "";
                        if (arrayOut.size() > JustMail.getInstance().getConfig().getMaxMailSize()) {
                            session.send(Response.NO_STORAGE_LEFT.create());
                        } else {
                            session.setData(arrayOut.toByteArray());
                            session.save();
                            session.send(Response.ACTION_OKAY.create());
                        }
                        session.resetMailData();
                        continue;
                    }
                    if (arrayOut.size() > JustMail.getInstance().getConfig().getMaxMailSize()) continue;
                    arrayOut.write(buffer, 0, read);
                    cache = "";
                    continue;
                }
                SmtpCommands.handleCommand(cache, session);
                cache = "";
            }

            in.close();
            out.close();

            socket.close();

            JustLogger.logger().fine("Closed connection from " + socket.getInetAddress().getHostAddress());
        } catch (IOException ignored) {
        }
    }

}
