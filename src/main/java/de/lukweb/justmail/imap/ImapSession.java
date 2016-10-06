package de.lukweb.justmail.imap;

import de.lukweb.justmail.socket.Session;

import java.io.IOException;
import java.net.Socket;

public class ImapSession extends Session {


    public ImapSession(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void sayGoodbye() {
        if (saidGoodbye) return;
        saidGoodbye = true;
        send(ImapResponse.BYE.create("*", "Server logging out"));
    }
}
