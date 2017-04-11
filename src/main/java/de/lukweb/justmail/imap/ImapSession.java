package de.lukweb.justmail.imap;

import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.socket.Session;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.utils.interfaces.CatchStreamCallback;

import java.io.IOException;
import java.net.Socket;

public class ImapSession extends Session {

    private User user;
    private String selectedMailbox;

    private CatchStreamCallback callback;

    public ImapSession(Socket socket) throws IOException {
        super(socket);
    }

    @Override
    public void sayGoodbye() {
        if (saidGoodbye) return;
        saidGoodbye = true;
        send(ImapResponse.BYE.create("*", "Server logging out"));
    }

    public boolean checkForAuthentication(String tag) {
        if (user != null) return true;
        send(ImapResponse.NO.create(tag, "Not authenticated"));
        return false;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    public CatchStreamCallback getCallback() {
        return callback;
    }

    public void setCallback(CatchStreamCallback callback) {
        this.callback = callback;
    }

    public String getSelectedMailbox() {
        return selectedMailbox;
    }

    public void setSelectedMailbox(String selectedMailbox) {
        this.selectedMailbox = selectedMailbox;
    }
}
