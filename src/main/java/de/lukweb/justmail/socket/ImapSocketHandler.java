package de.lukweb.justmail.socket;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.imap.ImapCommands;
import de.lukweb.justmail.imap.ImapResponse;
import de.lukweb.justmail.imap.ImapSession;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ImapSocketHandler implements Runnable {

    private static ArrayList<ImapSocketHandler> instances = new ArrayList<>();

    public static void stopAll() {
        instances.forEach(ImapSocketHandler::stop);
    }

    private Socket socket;

    public ImapSocketHandler(Socket socket) {
        this.socket = socket;
        instances.add(this);
    }

    @Override
    public void run() {
        if (Thread.currentThread().isInterrupted()) return;
        try {
            JustLogger.logger().fine("New connection from " + socket.getInetAddress().getHostAddress());

            String cache = "";
            ImapSession session = new ImapSession(socket);

            DataInputStream in = session.getIn();
            DataOutputStream out = session.getOut();

            session.send(ImapResponse.OK.create("*", "IMAP4rev1 Service Ready"));

            boolean telnetCommand = false;

            while (!Thread.currentThread().isInterrupted()) {
                /* if (session.isUpgradingToSSL()) try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                } */
                in = session.getIn();
                out = session.getOut();
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
                /* if (session.getCallback() != null) {
                    boolean breakCB = session.getCallback().callback(cache);
                    if (breakCB) session.setCallback(null);
                    cache = "";
                    continue;
                } */
                String[] split = cache.split(" ", 1);
                // We reject this because there's no tag or something else, but it's not okay
                if (split.length < 2) {
                    // todo send bad synatx
                    continue;
                }

                String tag = split[0];
                String command = split[1];

                ImapCommands.handleCommand(command, tag, session);

                cache = "";
            }

            in.close();
            out.close();

            socket.close();

            JustLogger.logger().fine("Closed connection from " + socket.getInetAddress().getHostAddress());
        } catch (IOException ignored) {
        }
    }

    public void stop() {
        if (socket == null || socket.isClosed()) return;
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // out.writeUTF();
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
