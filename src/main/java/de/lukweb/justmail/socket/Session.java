package de.lukweb.justmail.socket;

import de.lukweb.justmail.console.JustLogger;
import de.lukweb.justmail.crypto.CryptoUtils;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public abstract class Session {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private SSLSocket ssl;

    private boolean upgradingToSSL;
    protected boolean saidGoodbye;

    public Session(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
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

    public void upgradeToSSL() {
        upgradingToSSL = true;
        CryptoUtils.upgradeConnection(socket, true, sslSocket -> {
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

    public abstract void sayGoodbye();

    public void close() {
        if (!saidGoodbye) sayGoodbye();
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
