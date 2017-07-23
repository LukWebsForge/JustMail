package de.lukweb.justmail.imap;

import de.lukweb.justmail.imap.responses.ImapResponse;
import de.lukweb.justmail.socket.Session;
import de.lukweb.justmail.sql.objects.Mailbox;
import de.lukweb.justmail.sql.objects.User;
import de.lukweb.justmail.utils.interfaces.CatchStreamCallback;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ImapSession extends Session {

    private User user;
    private Mailbox selected;
    private List<Mailbox> subscribed;

    private CatchStreamCallback callback;

    public ImapSession(Socket socket) throws IOException {
        super(socket);
        this.subscribed = new ArrayList<>();
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

    public boolean checkForExistence(String tag, Mailbox mailbox) {
        if (mailbox != null) return true;
        send(ImapResponse.NO.create(tag, "Mailbox doesn't exists"));
        return false;
    }

    @Override
    public void send(String response) {
        response += "\r\n";
        super.send(response);
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

    public Mailbox getSelected() {
        return selected;
    }

    public void setSelected(Mailbox selected) {
        this.selected = selected;
    }

    public void addSubscribed(Mailbox mailbox) {
        this.subscribed.add(mailbox);
    }

    public void removeSubscribed(Mailbox mailbox) {
        this.subscribed.remove(mailbox);
    }

    public List<Mailbox> getSubscribed() {
        return subscribed;
    }
}
