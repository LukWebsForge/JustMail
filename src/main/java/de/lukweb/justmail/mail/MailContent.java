package de.lukweb.justmail.mail;

import de.lukweb.justmail.sql.objects.Mail;

import java.util.HashMap;

public class MailContent {

    private Mail mail;
    private HashMap<String, String> header = new HashMap<>();
    private HashMap<String, String> mimeparts = new HashMap<>();

    public MailContent(Mail mail) {
        this.mail = mail;
    }

    private void parse() {

    }

    public Mail getMail() {
        return mail;
    }
}
