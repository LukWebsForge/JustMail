package de.lukweb.justmail.smtp;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.crypto.CryptoUtils;
import de.lukweb.justmail.mail.EmailAdress;
import de.lukweb.justmail.sql.Storages;
import de.lukweb.justmail.sql.objects.Mail;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.sql.storages.Mails;
import de.lukweb.justmail.sql.storages.Users;
import de.lukweb.justmail.utils.interfaces.CatchStreamCallback;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class SmtpSession {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SSLSocket ssl;

    private String domain;

    private EmailAdress from;
    private EmailAdress to;

    private boolean readData;
    private byte[] data;

    private User user;

    private CatchStreamCallback callback;
    private boolean upgradingToSSL;

    public SmtpSession(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void send(String response) {
        if (upgradingToSSL) return;
        try {
            out.write(response.getBytes());
            out.flush();
        } catch (IOException e) {
            if (e instanceof SocketException && e.getMessage().toLowerCase().contains("broken pipe")) {
                JustLogger.logger().fine("Server from " + socket.getInetAddress().getHostAddress() + " closed " +
                        "connection!");
                close();
                return;
            }
            e.printStackTrace();
        }
    }

    public void resetMailData() {
        this.to = null;
        this.from = null;
        data = null;
        user = null;
    }

    public void resetAll() {
        this.domain = null;
        resetMailData();
        readData = false;
        data = null;
        user = null;
    }

    public void setReadData(boolean readData) {
        this.readData = readData;
    }

    public boolean isReadingData() {
        return readData;
    }

    public Socket getSocket() {
        return socket;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setFrom(EmailAdress from) {
        this.from = from;
    }

    public void setTo(EmailAdress to) {
        this.to = to;
    }

    public EmailAdress getFrom() {
        return from;
    }

    public EmailAdress getTo() {
        return to;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean checkForHello() {
        if (domain != null && !domain.trim().isEmpty()) return true;
        send(SmtpResponse.BAD_SEQUENCE.create());
        return false;
    }

    public boolean checkForForceSSL() {
        if (isUsingSSL()) return true;
        if (!JustMail.getInstance().getConfig().isForceSSL()) return true;
        send(SmtpResponse.MUST_USE_TLS.create());
        return false;
    }

    public boolean checkAuthentication() {
        if (isAuthenticated()) return true;
        send(SmtpResponse.BAD_SEQUENCE.create());
        return false;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void upgradeToSSL() {
        upgradingToSSL = true;
        ssl = CryptoUtils.upgradeConnection(socket, true, sslSocket -> {
            this.ssl = sslSocket;
            try {
                this.in = new DataInputStream(sslSocket.getInputStream());
                this.out = new DataOutputStream(sslSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            upgradingToSSL = false;
        });
    }

    public boolean isUpgradingToSSL() {
        return upgradingToSSL;
    }

    public boolean isUsingSSL() {
        return ssl != null;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public void save() {
        if (data == null) return;
        /*
        Concept of saving

        Out means the mail isn't from this server
        In means the mail is from this server

        Out->In = Save + IMAP Notification
        In->In = Don't connect again to this mail server. Just save it and send both a IMAP Notification
        In->Out = Connect to the other smtpd and send the mail

         */
        Users users = Storages.get(Users.class);
        Mails mails = Storages.get(Mails.class);

        boolean existsFrom = users.exists(from.getAdress());
        boolean existsTo = users.exists(to.getAdress());

        boolean sent = false; // false means, that we received this mail

        if (!existsFrom && existsTo) {

        } else if (existsFrom && existsTo) {
            mails.save(new Mail(from.getAdress(), from.getAdress(), to.getAdress(), false, 0, data));
            // todo imap notification
            sent = true;
        } else if (existsFrom && !existsTo) {
            sent = true;
        }

        if (!sent) {
            // todo junk check & imap notification
        }

        mails.save(new Mail(to.getAdress(), from.getAdress(), to.getAdress(), sent, 0, data));

        JustLogger.logger().fine("Recvied message from " + from.getAdress() + " to " + to.getAdress());
    }

    public CatchStreamCallback getCallback() {
        return callback;
    }

    public void setCallback(CatchStreamCallback callback) {
        this.callback = callback;
    }

    public void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
