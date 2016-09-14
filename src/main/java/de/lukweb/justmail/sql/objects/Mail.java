package de.lukweb.justmail.sql.objects;

import de.lukweb.justmail.sql.interfaces.Unquie;

public class Mail implements Unquie {

    private int id;
    private String from;
    private String to;
    private boolean sent;
    private int junkLevel; // A junk level from zero up to ten
    private String imapDirectory;
    private int date; // The date when the emails was recived or sent
    private byte[] content;

    public Mail(int id, String from, String to, boolean sent, int junkLevel, String imapDirectory, int date,
                byte[] content) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.sent = sent;
        this.junkLevel = junkLevel;
        this.imapDirectory = imapDirectory;
        this.date = date;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
