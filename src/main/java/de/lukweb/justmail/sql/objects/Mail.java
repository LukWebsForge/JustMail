package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.sql.interfaces.Unquie;

public class Mail implements Unquie {

    private int id;
    private String owner;
    private String from;
    private String to;
    private boolean sent;
    private int junkLevel; // A junk level from zero up to ten
    private String imapDirectory;
    private int date; // The date when the emails was recived or sent
    private byte[] content;
    private boolean read;

    public Mail(String owner, String from, String to, boolean sent, int junkLevel, byte[] content) {
        this.id = -1;
        this.owner = owner;
        this.from = from;
        this.to = to;
        this.sent = sent;
        this.junkLevel = junkLevel;
        this.imapDirectory = "unread"; // todo change this
        this.date = (int) (System.currentTimeMillis() / 1000);
        this.content = content;
        this.read = false;
    }

    public Mail(int id, String owner, String from, String to, boolean sent, int junkLevel, String imapDirectory,
                int date, byte[] content, boolean read) {
        this.id = id;
        this.owner = owner;
        this.from = from;
        this.to = to;
        this.sent = sent;
        this.junkLevel = junkLevel;
        this.imapDirectory = imapDirectory;
        this.date = date;
        this.content = content;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public boolean isSent() {
        return sent;
    }

    public int getJunkLevel() {
        return junkLevel;
    }

    public String getImapDirectory() {
        return imapDirectory;
    }

    public int getDate() {
        return date;
    }

    public byte[] getContent() {
        return content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
