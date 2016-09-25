package de.lukweb.justmail.smtp;

import de.lukweb.justmail.JustMail;
import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.utils.CryptoUtils;
import de.lukweb.justmail.utils.EmailAdress;
import de.lukweb.justmail.utils.interfaces.CatchStreamCallback;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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

    public SmtpSession(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void send(String response) {
        try {
            out.write(response.getBytes());
            out.flush();
        } catch (IOException e) {
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
        send(Response.BAD_SEQUENCE.create());
        return false;
    }

    public boolean checkForForceSSL() {
        if (isUsingSSL()) return true;
        if (!JustMail.getInstance().getConfig().isForceSSL()) return true;
        send(Response.MUST_USE_TLS.create());
        return false;
    }

    public boolean checkAuthentication() {
        if (isAuthenticated()) return true;
        send(Response.BAD_SEQUENCE.create());
        return false;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean upgradeToSSL() {
        ssl = CryptoUtils.upgradeConnection(socket);
        return isUsingSSL();
    }

    public boolean isUsingSSL() {
        return ssl != null;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public void save() {
        if (data == null) return;
        JustLogger.logger().info("Recvied message from " + from.getAdress() + " to " + to.getAdress() + ". " +
                "Data:\n " + new String(data));
    }

    public CatchStreamCallback getCallback() {
        return callback;
    }

    public void setCallback(CatchStreamCallback callback) {
        this.callback = callback;
    }
}
