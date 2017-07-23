package de.lukweb.justmail.socket;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.smtp.SmtpCommands;
import de.lukweb.justmail.smtp.SmtpResponse;
import de.lukweb.justmail.smtp.SmtpSession;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MailSocketHandler implements Runnable {

    private static ArrayList<MailSocketHandler> instances = new ArrayList<>();

    public static void stopAll() {
        instances.forEach(MailSocketHandler::stop);
    }

    private Socket socket;

    public MailSocketHandler(Socket socket) {
        this.socket = socket;
        instances.add(this);
    }

    public void run() {
        if (Thread.currentThread().isInterrupted()) return;
        try {
            JustLogger.logger().fine("New connection (SMTP) from " + socket.getInetAddress().getHostAddress());

            String cache = "";
            SmtpSession session = new SmtpSession(socket);

            String ownDomain = JustMail.getInstance().getConfig().getHost();

            // First we send greetings
            session.send(SmtpResponse.SERVICE_READY.create(ownDomain));

            boolean telnetCommand = false;

            while (!Thread.currentThread().isInterrupted()) {
                if (session.isUpgradingToSSL()) try {
                    Thread.sleep(1);
                    continue;
                } catch (InterruptedException e) {
                    break;
                }
                int read = session.getIn().read();
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
                if (session.getCallback() != null) {
                    boolean breakCB = session.getCallback().callback(cache);
                    if (breakCB) session.setCallback(null);
                    cache = "";
                    continue;
                }
                SmtpCommands.handleCommand(cache, session);
                cache = "";
            }

            session.close();

            JustLogger.logger().fine("Closed connection from " + socket.getInetAddress().getHostAddress());
        } catch (IOException ignored) {
        }
    }

    private void stop() {
        if (socket == null || socket.isClosed()) return;
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(SmtpResponse.SERVICE_CLOSING.create());
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

}
