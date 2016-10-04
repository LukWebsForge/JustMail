package de.lukweb.justmail.imap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ImapSession {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public ImapSession(Socket socket) throws IOException {
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

    public void send(String tag, String response) {
        send(tag + " " + response);
    }

    public void send(String response) {
        try {
            out.write(response.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
