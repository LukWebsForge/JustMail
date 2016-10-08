package de.lukweb.justmail.imap.responses;

public enum ImapResponse {

    OK("OK"),
    NO("NO"),
    BAD("BAD"),
    BYE("BYE");

    private String text;

    ImapResponse(String text) {
        this.text = text;
    }

    public String create(String tag, String description) {
        return tag + " " + text + " " + description;
    }

}
